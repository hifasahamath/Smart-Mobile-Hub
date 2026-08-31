'use client';

import { useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Product, Page } from '@/lib/types';
import ProductCard from '@/components/ProductCard';
import { Filter, Loader2 } from 'lucide-react';
import { Suspense, useState } from 'react';

function ProductsContent() {
  const searchParams = useSearchParams();
  const [page, setPage] = useState(0);

  const category = searchParams.get('category') || '';
  const brand = searchParams.get('brand') || '';
  const search = searchParams.get('search') || '';
  const featured = searchParams.get('featured') || '';
  const trending = searchParams.get('trending') || '';

  const queryKey = ['products', { page, category, brand, search, featured, trending }];

  const { data, isLoading, error } = useQuery({
    queryKey,
    queryFn: async () => {
      let path = `/api/v1/catalog/products?page=${page}&size=12`;
      if (category) path += `&category=${category}`;
      if (brand) path += `&brand=${brand}`;
      if (search) path += `&search=${search}`;
      if (featured) path += `&featured=${featured}`;
      if (trending) path += `&trending=${trending}`;
      const res = await api.get<Page<Product>>(path);
      return res.data;
    },
  });

  const title = search
    ? `Search: "${search}"`
    : category
    ? category.charAt(0).toUpperCase() + category.slice(1)
    : brand
    ? brand.charAt(0).toUpperCase() + brand.slice(1)
    : featured
    ? 'Featured Products'
    : trending
    ? 'Trending Now'
    : 'All Products';

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold">{title}</h1>
          {data && (
            <p className="text-sm text-muted mt-1">{data.totalElements} products found</p>
          )}
        </div>
        <button className="inline-flex items-center gap-2 px-4 py-2 bg-surface border border-border rounded-lg text-sm font-medium text-muted hover:text-foreground hover:border-primary/30 transition-all">
          <Filter className="w-4 h-4" /> Filter
        </button>
      </div>

      {/* Loading */}
      {isLoading && (
        <div className="flex items-center justify-center py-20">
          <Loader2 className="w-8 h-8 text-primary animate-spin" />
        </div>
      )}

      {/* Error */}
      {error && (
        <div className="text-center py-20">
          <p className="text-muted mb-2">Unable to load products</p>
          <p className="text-sm text-danger">{(error as Error).message}</p>
        </div>
      )}

      {/* Products Grid */}
      {data && (
        <>
          {data.content.length === 0 ? (
            <div className="text-center py-20">
              <p className="text-lg font-medium mb-2">No products found</p>
              <p className="text-sm text-muted">Try a different search or filter</p>
            </div>
          ) : (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 md:gap-6">
              {data.content.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}

          {/* Pagination */}
          {data.totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 mt-10">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 text-sm font-medium bg-surface border border-border rounded-lg hover:bg-surface-hover disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              >
                Previous
              </button>
              <span className="text-sm text-muted px-4">
                Page {page + 1} of {data.totalPages}
              </span>
              <button
                onClick={() => setPage(p => Math.min(data.totalPages - 1, p + 1))}
                disabled={page >= data.totalPages - 1}
                className="px-4 py-2 text-sm font-medium bg-surface border border-border rounded-lg hover:bg-surface-hover disabled:opacity-50 disabled:cursor-not-allowed transition-all"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default function ProductsPage() {
  return (
    <Suspense fallback={
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-8 h-8 text-primary animate-spin" />
      </div>
    }>
      <ProductsContent />
    </Suspense>
  );
}
