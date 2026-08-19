import { CommodityItem } from '../domain/models';

export function getMockSeedCommodities(): CommodityItem[] {
  const today = new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  return [
    {
      commodity_name: "Black Pepper (Garbled)",
      market_center: "Sirsi APMC",
      state: "Karnataka",
      variety: "Malabar Special",
      min_price: 58000,
      max_price: 64500,
      modal_price: 62000,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: 3.4,
      price_trend: "UP",
      self_healed: true
    },
    {
      commodity_name: "Small Cardamom (7-8mm)",
      market_center: "Vandanmettu Auction",
      state: "Kerala",
      variety: "Green Bold Grade-A",
      min_price: 215000,
      max_price: 248000,
      modal_price: 236000,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: 4.8,
      price_trend: "UP",
      self_healed: true
    },
    {
      commodity_name: "Turmeric (Finger)",
      market_center: "Erode APMC",
      state: "Tamil Nadu",
      variety: "Salem Rajapuri",
      min_price: 14200,
      max_price: 16800,
      modal_price: 15900,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: -1.2,
      price_trend: "DOWN",
      self_healed: true
    },
    {
      commodity_name: "Coriander Seeds (Eagle)",
      market_center: "Guntur Yard",
      state: "Andhra Pradesh",
      variety: "Badami Quality",
      min_price: 7800,
      max_price: 9200,
      modal_price: 8650,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: 0.5,
      price_trend: "STABLE",
      self_healed: true
    },
    {
      commodity_name: "Cumin / Jeera",
      market_center: "Unjha APMC",
      state: "Gujarat",
      variety: "Export Super Grade",
      min_price: 24500,
      max_price: 29800,
      modal_price: 27600,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: 2.1,
      price_trend: "UP",
      self_healed: true
    },
    {
      commodity_name: "Dry Ginger (Unbleached)",
      market_center: "Wayanad APMC",
      state: "Kerala",
      variety: "Nadan Grade-1",
      min_price: 28000,
      max_price: 33500,
      modal_price: 31200,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: 1.8,
      price_trend: "UP",
      self_healed: true
    },
    {
      commodity_name: "Red Chilli (Guntur Sannam)",
      market_center: "Khammam Yard",
      state: "Telangana",
      variety: "S334 Export Grade",
      min_price: 18500,
      max_price: 22400,
      modal_price: 20800,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: -0.8,
      price_trend: "DOWN",
      self_healed: true
    },
    {
      commodity_name: "Clove (Hand Picked)",
      market_center: "Kottayam APMC",
      state: "Kerala",
      variety: "Zanzibar Selection",
      min_price: 85000,
      max_price: 98000,
      modal_price: 92000,
      price_unit: "₹/Quintal",
      arrival_date: today,
      price_change_percent: 0.0,
      price_trend: "STABLE",
      self_healed: true
    }
  ];
}
