export const BRANDS = [

    { name: 'NIKE', id: 'nike', tagline: 'Just Do It' },
    { name: 'ADIDAS', id: 'adidas', tagline: 'Impossible Is Nothing' },
    { name: 'NEW BALANCE', id: 'new-balance', tagline: 'Fearlessly Independent' },
    { name: 'REEBOK', id: 'reebok', tagline: 'Classic & Heritage' },
    { name: 'THE NORTH FACE', id: 'the-north-face', tagline: 'Never Stop Exploring' },
    { name: 'VANS', id: 'vans', tagline: 'Off The Wall' },
] as const;

export type BrandId = typeof BRANDS[number]['id'];
