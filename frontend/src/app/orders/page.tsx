'use client';

import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { useAuth } from '@/lib/auth';
import type { Order, Page } from '@/lib/types';
import { Package, Loader2, Clock, CheckCircle, XCircle, Truck } from 'lucide-react';
import Link from 'next/link';

const statusConfig: Record<string, { icon: typeof Clock; color: string; label: string }> = {
  PENDING: { icon: Clock, color: 'text-warning', label: 'Pending' },
  CONFIRMED: { icon: CheckCircle, color: 'text-primary', label: 'Confirmed' },
  PROCESSING: { icon: Package, color: 'text-accent', label: 'Processing' },
  SHIPPED: { icon: Truck, color: 'text-blue-500', label: 'Shipped' },
  DELIVERED: { icon: CheckCircle, color: 'text-success', label: 'Delivered' },
  CANCELLED: { icon: XCircle, color: 'text-danger', label: 'Cancelled' },
};

export default function OrdersPage() {
  const { isLoggedIn } = useAuth();

  const { data, isLoading } = useQuery({
    queryKey: ['orders'],
    queryFn: async () => {
      const res = await api.get<Page<Order>>('/api/v1/orders?size=20');
      return res.data;
    },
    enabled: isLoggedIn,
  });

  if (!isLoggedIn) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-20 text-center">
        <h1 className="text-2xl font-bold mb-2">Sign in to view orders</h1>
        <Link href="/login?redirect=/orders" className="text-primary hover:text-primary-hover">Sign In</Link>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-8 h-8 text-primary animate-spin" />
      </div>
    );
  }

  const orders = data?.content || [];

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="text-2xl font-bold mb-8">My Orders</h1>

      {orders.length === 0 ? (
        <div className="text-center py-16">
          <Package className="w-16 h-16 text-border mx-auto mb-4" />
          <h2 className="text-lg font-semibold mb-2">No orders yet</h2>
          <p className="text-muted mb-6">Start shopping to place your first order</p>
          <Link href="/products" className="inline-flex px-6 py-3 bg-primary text-white rounded-xl font-medium hover:bg-primary-hover transition-all">
            Browse Products
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => {
            const status = statusConfig[order.status] || statusConfig.PENDING;
            const StatusIcon = status.icon;
            return (
              <div key={order.id} className="bg-surface border border-border rounded-2xl p-5 animate-fade-in">
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <span className="font-semibold text-sm">{order.orderNumber}</span>
                    <span className="text-xs text-muted ml-3">
                      {new Date(order.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                  <div className={`flex items-center gap-1.5 text-xs font-medium ${status.color}`}>
                    <StatusIcon className="w-4 h-4" />
                    {status.label}
                  </div>
                </div>
                <div className="text-sm text-muted mb-3">
                  {order.items.map(item => `${item.productName} × ${item.quantity}`).join(', ')}
                </div>
                <div className="flex items-center justify-between">
                  <span className="font-bold">Rs. {order.total.toLocaleString()}</span>
                  <span className="text-xs text-muted capitalize">{order.paymentMethod.toLowerCase().replace(/_/g, ' ')}</span>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
