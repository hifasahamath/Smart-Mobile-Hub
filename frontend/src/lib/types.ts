// --- Product Types ---
export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  imageUrl?: string;
  sortOrder: number;
  parentId?: number;
  children?: Category[];
}

export interface Brand {
  id: number;
  name: string;
  slug: string;
  logoUrl?: string;
  description?: string;
}

export interface ProductImage {
  id: number;
  url: string;
  altText?: string;
  sortOrder: number;
  primary: boolean;
}

export interface VariantOption {
  id: number;
  value: string;
  sortOrder: number;
}

export interface VariantGroup {
  id: number;
  name: string;
  sortOrder: number;
  options: VariantOption[];
}

export interface Sku {
  id: number;
  skuCode: string;
  price: number;
  compareAtPrice?: number;
  optionValues: VariantOption[];
}

export interface Product {
  id: number;
  name: string;
  slug: string;
  description?: string;
  shortDescription?: string;
  basePrice: number;
  compareAtPrice?: number;
  specifications?: string;
  featured: boolean;
  trending: boolean;
  active: boolean;
  brand: Brand;
  category: Category;
  images: ProductImage[];
  variantGroups: VariantGroup[];
  skus: Sku[];
  primaryImageUrl?: string;
}

// --- Order Types ---
export interface OrderItem {
  id: number;
  skuCode: string;
  productName: string;
  variantDescription?: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface Order {
  id: number;
  orderNumber: string;
  customerEmail: string;
  status: string;
  items: OrderItem[];
  subtotal: number;
  deliveryFee: number;
  total: number;
  deliveryMethod: string;
  deliveryAddress?: string;
  deliveryCity?: string;
  deliveryZoneName?: string;
  contactName: string;
  contactPhone: string;
  paymentMethod: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface DeliveryZone {
  id: number;
  name: string;
  deliveryFee: number;
  estimatedDays: number;
}

// --- Cart Types ---
export interface CartItem {
  skuCode: string;
  productName: string;
  productSlug: string;
  variantDescription: string;
  unitPrice: number;
  quantity: number;
  imageUrl?: string;
}

// --- Auth Types ---
export interface User {
  email: string;
  fullName: string;
  role: string;
}

// --- Paginated response ---
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
