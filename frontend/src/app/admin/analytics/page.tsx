'use client';

import { useAuth } from '@/lib/auth';
import { useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Loader2, TrendingUp, Search, Eye, ShoppingCart } from 'lucide-react';

export default function AdminAnalyticsPage() {
  const { isAdmin } = useAuth();
  const router = useRouter();
  const [days, setDays] = useState(30);

  useEffect(() => { if (!isAdmin) router.push('/login'); }, [isAdmin, router]);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-analytics-full', days],
    queryFn: async () => {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/api/v1/analytics/dashboard?days=${days}`,
        { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
      );
      return res.json();
    },
    enabled: isAdmin,
  });

  if (!isAdmin) return null;

  const analytics = data?.data || {};
  const eventCounts = analytics.eventCounts || {};
  const topProducts = analytics.topViewedProducts || [];
  const topSearches = analytics.topSearches || [];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-bold">Analytics</h1>
        <select value={days} onChange={e => setDays(Number(e.target.value))}
          className="px-3 py-2 bg-surface border border-border rounded-lg text-sm">
          <option value={7}>Last 7 days</option>
          <option value={30}>Last 30 days</option>
          <option value={90}>Last 90 days</option>
        </select>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20"><Loader2 className="w-8 h-8 text-primary animate-spin" /></div>
      ) : (
        <>
          {/* Event Counts */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
            {Object.entries(eventCounts).map(([type, count]) => (
              <div key={type} className="bg-surface border border-border rounded-2xl p-5">
                <p className="text-2xl font-bold">{String(count)}</p>
                <p className="text-xs text-muted mt-0.5 capitalize">{type.toLowerCase().replace(/_/g, ' ')}</p>
              </div>
            ))}
          </div>

          <div className="grid lg:grid-cols-2 gap-6">
            {/* Top Viewed Products */}
            <div className="bg-surface border border-border rounded-2xl p-5">
              <div className="flex items-center gap-2 mb-4">
                <Eye className="w-4 h-4 text-primary" />
                <h2 className="font-semibold">Top Viewed Products</h2>
              </div>
              {topProducts.length === 0 ? (
                <p className="text-sm text-muted py-4 text-center">No data yet</p>
              ) : (
                <div className="space-y-3">
                  {topProducts.slice(0, 10).map((item: { productData: string; views: number }, i: number) => (
                    <div key={i} className="flex items-center justify-between text-sm">
                      <span className="truncate mr-3">{item.productData}</span>
                      <span className="text-muted font-medium shrink-0">{item.views} views</span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Top Searches */}
            <div className="bg-surface border border-border rounded-2xl p-5">
              <div className="flex items-center gap-2 mb-4">
                <Search className="w-4 h-4 text-primary" />
                <h2 className="font-semibold">Top Searches</h2>
              </div>
              {topSearches.length === 0 ? (
                <p className="text-sm text-muted py-4 text-center">No data yet</p>
              ) : (
                <div className="space-y-3">
                  {topSearches.slice(0, 10).map((item: { query: string; count: number }, i: number) => (
                    <div key={i} className="flex items-center justify-between text-sm">
                      <span className="truncate mr-3">&quot;{item.query}&quot;</span>
                      <span className="text-muted font-medium shrink-0">{item.count} searches</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          <div className="mt-6 text-center text-xs text-muted">
            Total events tracked: {analytics.totalEvents || 0} &bull; Period: {analytics.period}
          </div>
        </>
      )}
    </div>
  );
}
