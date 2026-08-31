'use client';

import { useAuth } from '@/lib/auth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Order, Page } from '@/lib/types';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Loader2, Clock, CheckCircle, XCircle, Truck, Package } from 'lucide-react';

const statusConfig: Record<string, { icon: typeof Clock; color: string; bg: string }> = {
  PENDING: { icon: Clock, color: 'text-warning', bg: 'bg-warning/10' },
  CONFIRMED: { icon: CheckCircle, color: 'text-primary', bg: 'bg-primary/10' },
  PROCESSING: { icon: Package, color: 'text-accent', bg: 'bg-accent/10' },
  SHIPPED: { icon: Truck, color: 'text-blue-500', bg: 'bg-blue-500/10' },
  DELIVERED: { icon: CheckCircle, color: 'text-success', bg: 'bg-success/10' },
  CANCELLED: { icon: XCircle, color: 'text-danger', bg: 'bg-danger/10' },
};

const transitions: Record<string, string[]> = {
  PENDING: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['SHIPPED'],
  SHIPPED: ['DELIVERED'],
};

export default function AdminOrdersPage() {
  const { isAdmin } = useAuth();
  const router = useRouter();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);

  useEffect(() => { if (!isAdmin) router.push('/login'); }, [isAdmin, router]);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-orders', page],
    queryFn: async () => {
      const res = await api.get<Page<Order>>(`/api/v1/orders/all?page=${page}&size=15`);
      return res.data;
    },
    enabled: isAdmin,
  });

  const updateStatus = useMutation({
    mutationFn: async ({ id, status }: { id: number; status: string }) => {
      await api.put(`/api/v1/orders/${id}/status`, { status });
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-orders'] }),
  });

  if (!isAdmin) return null;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="text-2xl font-bold mb-8">Manage Orders</h1>

      {isLoading ? (
        <div className="flex justify-center py-20"><Loader2 className="w-8 h-8 text-primary animate-spin" /></div>
      ) : (
        <>
          <div className="space-y-3">
            {data?.content.map((order) => {
              const cfg = statusConfig[order.status] || statusConfig.PENDING;
              const StatusIcon = cfg.icon;
              const nextStates = transitions[order.status] || [];

              return (
                <div key={order.id} className="bg-surface border border-border rounded-2xl p-5 animate-fade-in">
                  <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
                    <div>
                      <span className="font-semibold">{order.orderNumber}</span>
                      <span className="text-xs text-muted ml-3">{order.customerEmail}</span>
                      <span className="text-xs text-muted ml-3">{new Date(order.createdAt).toLocaleDateString()}</span>
                    </div>
                    <div className={`flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${cfg.color} ${cfg.bg}`}>
                      <StatusIcon className="w-3.5 h-3.5" />
                      {order.status}
                    </div>
                  </div>

                  <div className="text-sm text-muted mb-3">
                    {order.items.map(i => `${i.productName} × ${i.quantity}`).join(', ')}
                  </div>

                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <div className="flex items-baseline gap-3">
                      <span className="font-bold">Rs. {order.total.toLocaleString()}</span>
                      <span className="text-xs text-muted capitalize">{order.paymentMethod.toLowerCase().replace(/_/g, ' ')}</span>
                      <span className="text-xs text-muted capitalize">{order.deliveryMethod.toLowerCase().replace(/_/g, ' ')}</span>
                    </div>

                    {nextStates.length > 0 && (
                      <div className="flex gap-2">
                        {nextStates.map((status) => (
                          <button
                            key={status}
                            onClick={() => updateStatus.mutate({ id: order.id, status })}
                            disabled={updateStatus.isPending}
                            className={`px-3 py-1.5 text-xs font-medium rounded-lg transition-all ${
                              status === 'CANCELLED'
                                ? 'bg-danger/10 text-danger hover:bg-danger/20'
                                : 'bg-primary/10 text-primary hover:bg-primary/20'
                            }`}
                          >
                            → {status}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
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
