import Link from 'next/link';
import { ShoppingCart, Star } from 'lucide-react';
import type { Product } from '@/lib/types';

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  const primaryImage = product.images?.find(img => img.primary)?.url || product.primaryImageUrl;
  const hasDiscount = product.compareAtPrice && product.compareAtPrice > product.basePrice;
  const discountPercent = hasDiscount
    ? Math.round(((product.compareAtPrice! - product.basePrice) / product.compareAtPrice!) * 100)
    : 0;

  return (
    <Link
      href={`/products/${product.slug}`}
      className="group relative bg-surface border border-border rounded-2xl overflow-hidden hover:border-primary/30 hover:shadow-xl hover:shadow-primary/5 transition-all duration-300"
    >
      {/* Image */}
      <div className="relative aspect-square bg-gradient-to-br from-surface-hover to-background overflow-hidden">
        {primaryImage ? (
          <img
            src={primaryImage}
            alt={product.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <ShoppingCart className="w-12 h-12 text-border" />
          </div>
        )}

        {/* Badges */}
        <div className="absolute top-3 left-3 flex flex-col gap-1.5">
          {hasDiscount && (
            <span className="px-2 py-0.5 bg-danger text-white text-[10px] font-bold rounded-md">
              -{discountPercent}%
            </span>
          )}
          {product.featured && (
            <span className="px-2 py-0.5 bg-accent text-white text-[10px] font-bold rounded-md flex items-center gap-1">
              <Star className="w-2.5 h-2.5" /> Featured
            </span>
          )}
          {product.trending && (
            <span className="px-2 py-0.5 bg-primary text-white text-[10px] font-bold rounded-md">
              Trending
            </span>
          )}
        </div>
      </div>

      {/* Info */}
      <div className="p-4">
        <p className="text-[10px] text-muted uppercase tracking-wider font-medium mb-1">
          {product.brand?.name}
        </p>
        <h3 className="font-semibold text-sm leading-snug mb-2 group-hover:text-primary transition-colors line-clamp-2">
          {product.name}
        </h3>
        <div className="flex items-baseline gap-2">
          <span className="font-bold text-lg">
            Rs. {product.basePrice.toLocaleString()}
          </span>
          {hasDiscount && (
            <span className="text-xs text-muted line-through">
              Rs. {product.compareAtPrice!.toLocaleString()}
            </span>
          )}
        </div>
      </div>
    </Link>
  );
}
