import { DCATriggerResponse } from '../domain/models';

export interface IBrightDataService {
  triggerDCABatch(): Promise<DCATriggerResponse>;
  getDCADataset(collectionId: string): Promise<{ status: string; collection_id: string; items?: any; message?: string }>;
}
