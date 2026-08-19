// Domain Model: Commodity Item Representation
export interface CommodityItem {
  commodity_name: string;
  market_center: string;
  state: string;
  variety: string;
  min_price: number;
  max_price: number;
  modal_price: number;
  price_unit: string;
  arrival_date: string;
  price_change_percent: number;
  price_trend: 'UP' | 'DOWN' | 'STABLE';
  self_healed: boolean;
}

// Domain Model: Job Cache Entry
export interface CacheEntry {
  status: 'PROCESSING' | 'READY';
  timestamp: number;
  data?: CommodityItem[];
}

// Domain Model: DCA Trigger Response
export interface DCATriggerResponse {
  collection_id: string;
  status: string;
  start_eta?: string;
  note?: string;
}
