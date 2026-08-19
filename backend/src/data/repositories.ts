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

    // Standard Gemini REST Payload
    const standardPayload = {
      contents: [{ parts: [{ text: prompt }] }]
    };

    // 1. Try Gemini 2.5 Flash (Latest General Availability)
    try {
      const response = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${this.apiKey}`,
        standardPayload,
        { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
      );
      return response.data;
    } catch (err1: any) {
      console.warn('gemini-2.5-flash failed, trying gemini-2.0-flash:', err1.message);
    }

    // 2. Try Gemini 2.0 Flash
    try {
      const response = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${this.apiKey}`,
        standardPayload,
        { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
      );
      return response.data;
    } catch (err2: any) {
      console.warn('gemini-2.0-flash failed, trying gemini-1.5-flash:', err2.message);
    }

    // 3. Try Gemini 1.5 Flash (Universal Stable Baseline)
    const fallbackResponse = await axios.post(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${this.apiKey}`,
      standardPayload,
      { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
    );
    return fallbackResponse.data;
  }
}
