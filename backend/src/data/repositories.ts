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

  async generateAdvisory(prompt: string, language: string = 'en'): Promise<any> {
    if (!this.apiKey || this.apiKey.includes('YOUR_')) {
      throw new Error('GEMINI_API_KEY is missing or invalid. Please configure your API key in environment secrets.');
    }

    // Multilingual Regional Indian Language Support Map
    const languageNames: Record<string, string> = {
      hi: 'Hindi (हिंदी)',
      ml: 'Malayalam (മലയാളം)',
      kn: 'Kannada (ಕನ್ನಡ)',
      ta: 'Tamil (தமிழ்)',
      te: 'Telugu (తెలుగు)',
      mr: 'Marathi (मराठी)',
      en: 'English'
    };

    const targetLang = languageNames[language] || 'English';

    // System instruction instructing Gemini to output advisory in the farmer's native language
    const fullPrompt = language && language !== 'en'
      ? `[Target Language: ${targetLang}]\nRespond exclusively in ${targetLang} using simple, encouraging agrarian terminology for smallholder farmers.\n\n${prompt}`
      : prompt;

    // Production Quota-Optimized Generation Config (Balanced thinking + complete output text)
    const payload = {
      contents: [{ parts: [{ text: fullPrompt }] }],
      generationConfig: {
        maxOutputTokens: 1000, // Balanced budget (~400 thinking tokens + ~600 output text tokens)
        temperature: 0.3,      // Deterministic advisory outputs
        thinkingConfig: {
          thinkingLevel: 'LOW' // Fast reasoning without token bloat
        }
      }
    };

    // 1. Try Primary: gemini-3.6-flash
    try {
      const response = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=${this.apiKey}`,
        payload,
        { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
      );
      return response.data;
    } catch (err1: any) {
      console.warn('gemini-3.6-flash failed, trying gemini-3.7-flash:', err1.message);
    }

    // 2. Try Secondary: gemini-3.7-flash
    try {
      const response = await axios.post(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.7-flash:generateContent?key=${this.apiKey}`,
        payload,
        { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
      );
      return response.data;
    } catch (err2: any) {
      console.warn('gemini-3.7-flash failed, trying gemini-flash-latest:', err2.message);
    }

    // 3. Fallback: gemini-flash-latest
    const fallbackResponse = await axios.post(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=${this.apiKey}`,
      payload,
      { headers: { 'Content-Type': 'application/json' }, timeout: 20000 }
    );
    return fallbackResponse.data;
  }
}
