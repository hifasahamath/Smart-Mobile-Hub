'use client';

import { useAuth } from '@/lib/auth';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { Loader2, Package, AlertTriangle } from 'lucide-react';

interface InventoryRecord {
  id: number;
  skuCode: string;
  productName: string;
  quantityOnHand: number;
  quantityReserved: number;
  reorderPoint: number;
  lastUpdated: string;
}

interface PageData {
  content: InventoryRecord[];
  totalPages: number;
  totalElements: number;
}

export default function AdminInventoryPage() {
  const { isAdmin } = useAuth();
  const router = useRouter();

  useEffect(() => { if (!isAdmin) router.push('/login'); }, [isAdmin, router]);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-inventory'],
    queryFn: async () => {
      const res = await api.get<PageData>('/api/v1/inventory?size=50');
      return res.data;
    },
    enabled: isAdmin,
  });

  if (!isAdmin) return null;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="text-2xl font-bold mb-2">Inventory Management</h1>
      <p className="text-sm text-muted mb-8">Monitor stock levels across all SKUs</p>

      {isLoading ? (
        <div className="flex justify-center py-20"><Loader2 className="w-8 h-8 text-primary animate-spin" /></div>
      ) : (
        <div className="bg-surface border border-border rounded-2xl overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border bg-surface-hover">
                  <th className="text-left px-4 py-3 font-medium text-muted">SKU Code</th>
                  <th className="text-left px-4 py-3 font-medium text-muted">Product</th>
                  <th className="text-right px-4 py-3 font-medium text-muted">On Hand</th>
                  <th className="text-right px-4 py-3 font-medium text-muted">Reserved</th>
                  <th className="text-right px-4 py-3 font-medium text-muted">Available</th>
                  <th className="text-center px-4 py-3 font-medium text-muted">Status</th>
                </tr>
              </thead>
              <tbody>
                {data?.content.map((item) => {
                  const available = item.quantityOnHand - item.quantityReserved;
                  const isLow = available <= item.reorderPoint;
                  const isOut = available <= 0;

                  return (
                    <tr key={item.id} className="border-b border-border last:border-0 hover:bg-surface-hover transition-colors">
                      <td className="px-4 py-3 font-mono text-xs">{item.skuCode}</td>
                      <td className="px-4 py-3">{item.productName || '—'}</td>
                      <td className="px-4 py-3 text-right font-medium">{item.quantityOnHand}</td>
                      <td className="px-4 py-3 text-right text-muted">{item.quantityReserved}</td>
                      <td className="px-4 py-3 text-right font-bold">{available}</td>
                      <td className="px-4 py-3 text-center">
                        {isOut ? (
                          <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-danger/10 text-danger text-[10px] font-bold rounded-full">
                            <AlertTriangle className="w-3 h-3" /> Out of Stock
                          </span>
                        ) : isLow ? (
                          <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-warning/10 text-warning text-[10px] font-bold rounded-full">
                            <AlertTriangle className="w-3 h-3" /> Low Stock
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-success/10 text-success text-[10px] font-bold rounded-full">
                            <Package className="w-3 h-3" /> In Stock
                          </span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
