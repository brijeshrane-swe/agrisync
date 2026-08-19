import { CommodityItem, CacheEntry, DCATriggerResponse } from '../domain/models';

export interface IBrightDataService {
  triggerDCABatch(): Promise<DCATriggerResponse>;
  getDCADataset(collectionId: string): Promise<{ status: string; collection_id: string; items?: any; message?: string }>;
}

export interface ICacheRepository {
  get(key: string): CacheEntry | undefined;
  set(key: string, entry: CacheEntry): void;
  has(key: string): boolean;
}
