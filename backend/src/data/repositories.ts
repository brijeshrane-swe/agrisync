import axios from 'axios';
import { IBrightDataService, IGeminiService } from '../domain/repositories';
import { DCATriggerResponse } from '../domain/models';

export class BrightDataService implements IBrightDataService {
  constructor(
    private apiKey: string,
    private collectorId: string,
    private targetUrl: string
  ) {}

  async triggerDCABatch(): Promise<DCATriggerResponse> {
    if (!this.apiKey || this.apiKey.includes('YOUR_')) {
      throw new Error('BRIGHTDATA_API_KEY is missing or invalid. Please configure your API key in environment secrets.');
    }

    const response = await axios.post(
      `https://api.brightdata.com/dca/trigger?collector=${this.collectorId}`,
      [{ url: this.targetUrl }],
      {
        headers: {
          Authorization: `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json'
        },
        timeout: 15000
      }
    );

    return response.data;
  }

  async getDCADataset(collectionId: string): Promise<{ status: string; collection_id: string; items?: any; message?: string }> {
    if (!this.apiKey || this.apiKey.includes('YOUR_')) {
      throw new Error('BRIGHTDATA_API_KEY is missing or invalid. Please configure your API key in environment secrets.');
    }

    try {
      const response = await axios.get(
        `https://api.brightdata.com/dca/dataset?id=${collectionId}`,
        {
          headers: {
            Authorization: `Bearer ${this.apiKey}`
          },
          timeout: 15000
        }
      );

      return {
        status: 'READY',
        collection_id: collectionId,
        items: response.data
      };
    } catch (error: any) {
      if (error.response?.status === 202) {
        return {
          status: 'PROCESSING',
          collection_id: collectionId,
          message: 'Bright Data scraper is executing DOM extraction.'
        };
      }
      throw error;
    }
  }
}

export class GeminiService implements IGeminiService {
  constructor(private apiKey: string) {}

  async generateAdvisory(prompt: string): Promise<any> {
    if (!this.apiKey || this.apiKey.includes('YOUR_')) {
      throw new Error('GEMINI_API_KEY is missing or invalid. Please configure your API key in environment secrets.');
    }

    const payload = {
      contents: [{ parts: [{ text: prompt }] }],
      generationConfig: {
        thinkingConfig: { thinkingLevel: 'HIGH' }
      },
      systemInstruction: {
        parts: [{ text: 'You are an expert agrarian market intelligence system helping farmers maximize crop revenue while minimizing storage and volatility risks.' }]
      }
    };

    try {
      // Try primary model: gemini-3.1-pro-preview with thinking mode HIGH
      const response = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=${this.apiKey}`,
        payload,
        { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
      );
      return response.data;
    } catch (primaryError: any) {
      console.warn('Primary Gemini model failed, trying fallback gemini-3.7-flash:', primaryError.message);
      // Fallback model: gemini-3.7-flash
      const fallbackResponse = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.7-flash:generateContent?key=${this.apiKey}`,
        payload,
        { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
      );
      return fallbackResponse.data;
    }
  }
}
