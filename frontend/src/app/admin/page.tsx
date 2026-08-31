'use client';

import { useAuth } from '@/lib/auth';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import {
  Package, ShoppingCart, CreditCard, Users, TrendingUp,
  BarChart3, Bell, Loader2, ArrowRight,
} from 'lucide-react';

export default function AdminDashboard() {
  const { isLoggedIn, isAdmin } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoggedIn || !isAdmin) {
      router.push('/login');
    }
  }, [isLoggedIn, isAdmin, router]);

  const { data: analyticsData, isLoading } = useQuery({
    queryKey: ['admin-analytics'],
    queryFn: async () => {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/api/v1/analytics/dashboard?days=30`,
        { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
      );
      return res.json();
    },
    enabled: isAdmin,
  });

  if (!isAdmin) return null;

  const stats = analyticsData?.data?.eventCounts || {};

  const cards = [
    { title: 'Orders', value: stats.ORDER_PLACED || '—', icon: ShoppingCart, color: 'from-violet-500 to-purple-600', href: '/admin/orders' },
    { title: 'Products', value: '30+', icon: Package, color: 'from-emerald-500 to-teal-600', href: '/admin/products' },
    { title: 'Payments', value: stats.CHECKOUT_START || '—', icon: CreditCard, color: 'from-amber-500 to-orange-600', href: '/admin/payments' },
    { title: 'Page Views', value: stats.PAGE_VIEW || '—', icon: TrendingUp, color: 'from-blue-500 to-indigo-600', href: '#' },
  ];

  const quickActions = [
    { title: 'Manage Orders', description: 'View and update order statuses', icon: ShoppingCart, href: '/admin/orders' },
    { title: 'Manage Products', description: 'Add, edit, or remove products', icon: Package, href: '/admin/products' },
    { title: 'Verify Payments', description: 'Review pending bank transfers', icon: CreditCard, href: '/admin/payments' },
    { title: 'Manage Inventory', description: 'Stock levels and adjustments', icon: BarChart3, href: '/admin/inventory' },
    { title: 'Notifications', description: 'View failed notifications', icon: Bell, href: '/admin/notifications' },
    { title: 'Analytics', description: 'View dashboard analytics', icon: TrendingUp, href: '/admin/analytics' },
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold">Admin Dashboard</h1>
          <p className="text-sm text-muted mt-1">Welcome back! Here&apos;s what&apos;s happening.</p>
        </div>
        <div className="flex items-center gap-2 px-3 py-1.5 bg-primary/10 text-primary text-xs font-semibold rounded-full">
          <Users className="w-3.5 h-3.5" /> Admin
        </div>
      </div>

      {/* Stats Cards */}
      {isLoading ? (
        <div className="flex justify-center py-10"><Loader2 className="w-6 h-6 text-primary animate-spin" /></div>
      ) : (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {cards.map((card) => (
            <Link
              key={card.title}
              href={card.href}
              className="bg-surface border border-border rounded-2xl p-5 hover:border-primary/30 hover:shadow-lg transition-all group"
            >
              <div className={`w-10 h-10 rounded-xl bg-gradient-to-br ${card.color} flex items-center justify-center mb-3 group-hover:scale-110 transition-transform`}>
                <card.icon className="w-5 h-5 text-white" />
              </div>
              <p className="text-2xl font-bold">{card.value}</p>
              <p className="text-xs text-muted mt-0.5">{card.title} (30 days)</p>
            </Link>
          ))}
        </div>
      )}

      {/* Quick Actions */}
      <h2 className="font-semibold text-lg mb-4">Quick Actions</h2>
      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {quickActions.map((action) => (
          <Link
            key={action.title}
            href={action.href}
            className="bg-surface border border-border rounded-2xl p-5 hover:border-primary/30 hover:shadow-lg transition-all group flex items-start gap-4"
          >
            <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center shrink-0 group-hover:bg-primary/20 transition-colors">
              <action.icon className="w-5 h-5 text-primary" />
            </div>
            <div className="flex-1 min-w-0">
              <h3 className="font-semibold text-sm group-hover:text-primary transition-colors">{action.title}</h3>
              <p className="text-xs text-muted mt-0.5">{action.description}</p>
            </div>
            <ArrowRight className="w-4 h-4 text-muted group-hover:text-primary shrink-0 mt-1 group-hover:translate-x-1 transition-all" />
          </Link>
        ))}
      </div>
    </div>
  );
}
