'use client';

import { useAuth } from '@/lib/auth';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Product, Page } from '@/lib/types';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Loader2, Package, Star, TrendingUp } from 'lucide-react';

export default function AdminProductsPage() {
  const { isAdmin } = useAuth();
  const router = useRouter();
  const [page, setPage] = useState(0);

  useEffect(() => { if (!isAdmin) router.push('/login'); }, [isAdmin, router]);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-products', page],
    queryFn: async () => {
      const res = await api.get<Page<Product>>(`/api/v1/products?page=${page}&size=15`);
      return res.data;
    },
    enabled: isAdmin,
  });

  if (!isAdmin) return null;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold">Products</h1>
          {data && <p className="text-sm text-muted mt-1">{data.totalElements} products total</p>}
        </div>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20"><Loader2 className="w-8 h-8 text-primary animate-spin" /></div>
      ) : (
        <>
          <div className="bg-surface border border-border rounded-2xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-border bg-surface-hover">
                    <th className="text-left px-4 py-3 font-medium text-muted">Product</th>
                    <th className="text-left px-4 py-3 font-medium text-muted">Brand</th>
                    <th className="text-left px-4 py-3 font-medium text-muted">Category</th>
                    <th className="text-right px-4 py-3 font-medium text-muted">Price</th>
                    <th className="text-center px-4 py-3 font-medium text-muted">SKUs</th>
                    <th className="text-center px-4 py-3 font-medium text-muted">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.content.map((product) => (
                    <tr key={product.id} className="border-b border-border last:border-0 hover:bg-surface-hover transition-colors">
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-surface-hover to-background flex items-center justify-center shrink-0">
                            {product.primaryImageUrl ? (
                              <img src={product.primaryImageUrl} alt="" className="w-full h-full object-cover rounded-lg" />
                            ) : (
                              <Package className="w-5 h-5 text-border" />
                            )}
                          </div>
                          <div className="min-w-0">
                            <p className="font-medium truncate">{product.name}</p>
                            <p className="text-xs text-muted">{product.slug}</p>
                          </div>
                        </div>
                      </td>
                      <td className="px-4 py-3 text-muted">{product.brand?.name}</td>
                      <td className="px-4 py-3 text-muted">{product.category?.name}</td>
                      <td className="px-4 py-3 text-right font-medium">Rs. {product.basePrice.toLocaleString()}</td>
                      <td className="px-4 py-3 text-center">{product.skus?.length || 0}</td>
                      <td className="px-4 py-3 text-center">
                        <div className="flex items-center justify-center gap-1.5">
                          {product.featured && (
                            <span className="px-1.5 py-0.5 bg-accent/10 text-accent text-[10px] font-bold rounded">
                              <Star className="w-2.5 h-2.5 inline" /> Featured
                            </span>
                          )}
                          {product.trending && (
                            <span className="px-1.5 py-0.5 bg-primary/10 text-primary text-[10px] font-bold rounded">
                              <TrendingUp className="w-2.5 h-2.5 inline" /> Trending
                            </span>
                          )}
                          {!product.featured && !product.trending && (
                            <span className="text-xs text-muted">Active</span>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {data && data.totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 mt-8">
              <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}
                className="px-4 py-2 text-sm bg-surface border border-border rounded-lg hover:bg-surface-hover disabled:opacity-50 transition-all">Previous</button>
              <span className="text-sm text-muted px-4">Page {page + 1} of {data.totalPages}</span>
              <button onClick={() => setPage(p => p + 1)} disabled={page >= data.totalPages - 1}
                className="px-4 py-2 text-sm bg-surface border border-border rounded-lg hover:bg-surface-hover disabled:opacity-50 transition-all">Next</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
