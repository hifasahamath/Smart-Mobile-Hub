'use client';

import { useAuth } from '@/lib/auth';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { Loader2, AlertTriangle, CheckCircle, Bell } from 'lucide-react';

interface Notification {
  id: number;
  recipientEmail: string;
  subject: string;
  body: string;
  type: string;
  status: string;
  referenceId?: string;
  errorMessage?: string;
  createdAt: string;
  sentAt?: string;
}

interface PageData {
  content: Notification[];
  totalPages: number;
}

export default function AdminNotificationsPage() {
  const { isAdmin } = useAuth();
  const router = useRouter();

  useEffect(() => { if (!isAdmin) router.push('/login'); }, [isAdmin, router]);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-notifications-failed'],
    queryFn: async () => {
      const res = await api.get<PageData>('/api/v1/notifications/failed?size=20');
      return res.data;
    },
    enabled: isAdmin,
  });

  if (!isAdmin) return null;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="text-2xl font-bold mb-2">Notifications</h1>
      <p className="text-sm text-muted mb-8">Monitor failed email notifications</p>

      {isLoading ? (
        <div className="flex justify-center py-20"><Loader2 className="w-8 h-8 text-primary animate-spin" /></div>
      ) : data?.content.length === 0 ? (
        <div className="text-center py-16 bg-surface border border-border rounded-2xl">
          <CheckCircle className="w-12 h-12 text-success mx-auto mb-3" />
          <h2 className="font-semibold mb-1">All notifications sent successfully</h2>
          <p className="text-sm text-muted">No failed notifications to report</p>
        </div>
      ) : (
        <div className="space-y-3">
          {data?.content.map((notif) => (
            <div key={notif.id} className="bg-surface border border-danger/20 rounded-2xl p-5 animate-fade-in">
              <div className="flex items-start gap-3">
                <AlertTriangle className="w-5 h-5 text-danger shrink-0 mt-0.5" />
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-semibold text-sm">{notif.subject}</span>
                    <span className="text-[10px] px-1.5 py-0.5 bg-danger/10 text-danger rounded font-medium">{notif.type.replace(/_/g, ' ')}</span>
                  </div>
                  <p className="text-xs text-muted mb-2">To: {notif.recipientEmail} &bull; {new Date(notif.createdAt).toLocaleString()}</p>
                  {notif.errorMessage && (
                    <p className="text-xs text-danger bg-danger/5 px-3 py-2 rounded-lg font-mono">{notif.errorMessage}</p>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
