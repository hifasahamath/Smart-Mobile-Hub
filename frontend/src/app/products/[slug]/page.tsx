'use client';

import { useParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { useCart } from '@/lib/cart';
import type { Product, Sku } from '@/lib/types';
import { ShoppingCart, Check, Minus, Plus, Loader2, Shield, Truck, ArrowLeft } from 'lucide-react';
import { useState } from 'react';
import Link from 'next/link';

export default function ProductDetailPage() {
  const params = useParams();
  const slug = params.slug as string;
  const { addItem } = useCart();
  const [quantity, setQuantity] = useState(1);
  const [selectedOptions, setSelectedOptions] = useState<Record<string, string>>({});
  const [added, setAdded] = useState(false);

  const { data: product, isLoading } = useQuery({
    queryKey: ['product', slug],
    queryFn: async () => {
      const res = await api.get<Product>(`/api/v1/products/slug/${slug}`);
      return res.data;
    },
  });

  const findMatchingSku = (): Sku | undefined => {
    if (!product?.skus?.length) return undefined;
    if (Object.keys(selectedOptions).length === 0) return product.skus[0];

    return product.skus.find(sku =>
      sku.optionValues.every(ov =>
        selectedOptions[ov.id?.toString() || ''] === ov.value ||
        Object.values(selectedOptions).includes(ov.value)
      )
    ) || product.skus[0];
  };

  const selectedSku = findMatchingSku();
  const price = selectedSku?.price ?? product?.basePrice ?? 0;
  const comparePrice = selectedSku?.compareAtPrice ?? product?.compareAtPrice;

  const handleAddToCart = () => {
    if (!product) return;
    const variantDesc = Object.values(selectedOptions).filter(Boolean).join(' / ');
    addItem({
      skuCode: selectedSku?.skuCode || `PROD-${product.id}`,
      productName: product.name,
      productSlug: product.slug,
      variantDescription: variantDesc || 'Default',
      unitPrice: price,
      quantity,
      imageUrl: product.images?.[0]?.url || product.primaryImageUrl,
    });
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-8 h-8 text-primary animate-spin" />
      </div>
    );
  }

  if (!product) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-20 text-center">
        <h1 className="text-2xl font-bold mb-2">Product not found</h1>
        <Link href="/products" className="text-primary hover:text-primary-hover">Browse all products</Link>
      </div>
    );
  }

  const primaryImage = product.images?.find(img => img.primary)?.url || product.primaryImageUrl;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      {/* Breadcrumb */}
      <Link href="/products" className="inline-flex items-center gap-1 text-sm text-muted hover:text-foreground mb-6 transition-colors">
        <ArrowLeft className="w-4 h-4" /> Back to Products
      </Link>

      <div className="grid md:grid-cols-2 gap-8 lg:gap-12">
        {/* Image */}
        <div className="relative aspect-square bg-surface border border-border rounded-2xl overflow-hidden">
          {primaryImage ? (
            <img src={primaryImage} alt={product.name} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-surface-hover to-background">
              <ShoppingCart className="w-20 h-20 text-border" />
            </div>
          )}
        </div>

        {/* Info */}
        <div>
          <p className="text-xs text-muted uppercase tracking-wider font-medium mb-2">{product.brand?.name}</p>
          <h1 className="text-2xl md:text-3xl font-bold mb-4">{product.name}</h1>

          {/* Price */}
          <div className="flex items-baseline gap-3 mb-6">
            <span className="text-3xl font-bold text-primary">Rs. {price.toLocaleString()}</span>
            {comparePrice && comparePrice > price && (
              <span className="text-lg text-muted line-through">Rs. {comparePrice.toLocaleString()}</span>
            )}
          </div>

          {/* Description */}
          {product.shortDescription && (
            <p className="text-muted text-sm mb-6 leading-relaxed">{product.shortDescription}</p>
          )}

          {/* Variant Options */}
          {product.variantGroups?.map((group) => (
            <div key={group.id} className="mb-5">
              <label className="text-sm font-semibold mb-2 block">{group.name}</label>
              <div className="flex flex-wrap gap-2">
                {group.options.map((option) => (
                  <button
                    key={option.id}
                    onClick={() => setSelectedOptions(prev => ({ ...prev, [group.name]: option.value }))}
                    className={`px-4 py-2 text-sm rounded-lg border transition-all ${
                      selectedOptions[group.name] === option.value
                        ? 'border-primary bg-primary/10 text-primary font-medium'
                        : 'border-border hover:border-primary/30'
                    }`}
                  >
                    {option.value}
                  </button>
                ))}
              </div>
            </div>
          ))}

          {/* Quantity */}
          <div className="mb-6">
            <label className="text-sm font-semibold mb-2 block">Quantity</label>
            <div className="inline-flex items-center border border-border rounded-lg">
              <button
                onClick={() => setQuantity(q => Math.max(1, q - 1))}
                className="p-2.5 hover:bg-surface-hover transition-colors"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="px-5 text-sm font-medium">{quantity}</span>
              <button
                onClick={() => setQuantity(q => q + 1)}
                className="p-2.5 hover:bg-surface-hover transition-colors"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Add to Cart */}
          <button
            onClick={handleAddToCart}
            className={`w-full py-3.5 rounded-xl font-semibold text-white transition-all flex items-center justify-center gap-2 ${
              added
                ? 'bg-success'
                : 'bg-primary hover:bg-primary-hover'
            }`}
          >
            {added ? (
              <><Check className="w-5 h-5" /> Added to Cart!</>
            ) : (
              <><ShoppingCart className="w-5 h-5" /> Add to Cart — Rs. {(price * quantity).toLocaleString()}</>
            )}
          </button>

          {/* Trust badges */}
          <div className="grid grid-cols-2 gap-3 mt-6">
            <div className="flex items-center gap-2 text-xs text-muted">
              <Shield className="w-4 h-4 text-success" />
              Genuine Product
            </div>
            <div className="flex items-center gap-2 text-xs text-muted">
              <Truck className="w-4 h-4 text-primary" />
              Island-wide Delivery
            </div>
          </div>

          {/* Specifications */}
          {product.specifications && (
            <div className="mt-8 pt-8 border-t border-border">
              <h3 className="font-semibold mb-3">Specifications</h3>
              <div className="text-sm text-muted leading-relaxed whitespace-pre-line">
                {product.specifications}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
