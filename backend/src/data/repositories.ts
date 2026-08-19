import axios from 'axios';
import { IBrightDataService, ICacheRepository } from '../domain/repositories';
import { CacheEntry, DCATriggerResponse } from '../domain/models';
import { getMockSeedCommodities } from './seedData';

export class InMemoryCacheRepository implements ICacheRepository {
  private cache = new Map<string, CacheEntry>();

  get(key: string): CacheEntry | undefined {
    return this.cache.get(key);
  }

  set(key: string, entry: CacheEntry): void {
    this.cache.set(key, entry);
  }

  has(key: string): boolean {
    return this.cache.has(key);
  }
}

export class BrightDataService implements IBrightDataService {
  constructor(
    private apiKey: string,
    private collectorId: string,
    private targetUrl: string,
    private cache: ICacheRepository
  ) {}

  async triggerDCABatch(): Promise<DCATriggerResponse> {
    const isKeyPlaceholder = !this.apiKey || this.apiKey.includes('YOUR_') || this.apiKey.includes('CHANGE_ME');

    if (isKeyPlaceholder) {
      const fakeCollectionId = `j_batch_${Math.floor(100000 + Math.random() * 900000)}`;
      this.cache.set(fakeCollectionId, {
        status: 'READY',
        timestamp: Date.now(),
        data: getMockSeedCommodities()
      });

      return {
        collection_id: fakeCollectionId,
        status: 'SCHEDULED',
        start_eta: '2s',
        note: 'Demo mode: using calibrated APMC seed data. Set BRIGHTDATA_API_KEY on Render for live triggers.'
      };
    }

    const response = await axios.post(
      `https://api.brightdata.com/dca/trigger?collector=${this.collectorId}`,
      [{ url: this.targetUrl }],
      {
        headers: {
          Authorization: `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json'
        },
        timeout: 10000
      }
    );

    const collectionId: string = response.data.collection_id;
    this.cache.set(collectionId, {
      status: 'PROCESSING',
      timestamp: Date.now()
    });

    return response.data;
  }

  async getDCADataset(collectionId: string): Promise<{ status: string; collection_id: string; items?: any; message?: string }> {
    const isKeyPlaceholder = !this.apiKey || this.apiKey.includes('YOUR_');

    if (isKeyPlaceholder || this.cache.has(collectionId)) {
      const cached = this.cache.get(collectionId);
      if (cached && cached.data) {
        return {
          status: 'READY',
          collection_id: collectionId,
          items: cached.data
        };
      }
    }

    try {
      const response = await axios.get(
        `https://api.brightdata.com/dca/dataset?id=${collectionId}`,
        {
          headers: {
            Authorization: `Bearer ${this.apiKey}`
          },
          timeout: 10000
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
