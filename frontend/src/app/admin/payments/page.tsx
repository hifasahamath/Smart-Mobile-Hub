'use client';

import { useAuth } from '@/lib/auth';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { Loader2, CheckCircle, XCircle, Clock, Upload } from 'lucide-react';

interface PaymentRecord {
  id: number;
  paymentReference: string;
  orderNumber: string;
  customerEmail: string;
  paymentMethod: string;
  status: string;
  amount: number;
  receiptUrl?: string;
  verificationNotes?: string;
  verifiedBy?: string;
  createdAt: string;
}

interface PageData {
  content: PaymentRecord[];
  totalPages: number;
  totalElements: number;
}

export default function AdminPaymentsPage() {
  const { isAdmin } = useAuth();
  const router = useRouter();
  const queryClient = useQueryClient();

  useEffect(() => { if (!isAdmin) router.push('/login'); }, [isAdmin, router]);

  const { data, isLoading } = useQuery({
    queryKey: ['admin-payments-pending'],
    queryFn: async () => {
      const res = await api.get<PageData>('/api/v1/payments/pending?size=20');
      return res.data;
    },
    enabled: isAdmin,
  });

  const verifyPayment = useMutation({
    mutationFn: async ({ id, approved, notes }: { id: number; approved: boolean; notes: string }) => {
      await api.post(`/api/v1/payments/${id}/verify`, { approved: String(approved), notes });
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['admin-payments-pending'] }),
  });

  if (!isAdmin) return null;

  const statusIcon = (status: string) => {
    switch (status) {
      case 'RECEIPT_UPLOADED': return <Upload className="w-3.5 h-3.5 text-warning" />;
      case 'VERIFIED': return <CheckCircle className="w-3.5 h-3.5 text-success" />;
      case 'REJECTED': return <XCircle className="w-3.5 h-3.5 text-danger" />;
      default: return <Clock className="w-3.5 h-3.5 text-muted" />;
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="text-2xl font-bold mb-2">Payment Verification</h1>
      <p className="text-sm text-muted mb-8">Review and verify bank transfer receipts</p>

      {isLoading ? (
        <div className="flex justify-center py-20"><Loader2 className="w-8 h-8 text-primary animate-spin" /></div>
      ) : data?.content.length === 0 ? (
        <div className="text-center py-16 bg-surface border border-border rounded-2xl">
          <CheckCircle className="w-12 h-12 text-success mx-auto mb-3" />
          <h2 className="font-semibold mb-1">All caught up!</h2>
          <p className="text-sm text-muted">No pending payment verifications</p>
        </div>
      ) : (
        <div className="space-y-4">
          {data?.content.map((payment) => (
            <div key={payment.id} className="bg-surface border border-border rounded-2xl p-5 animate-fade-in">
              <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
                <div>
                  <span className="font-semibold text-sm">{payment.paymentReference}</span>
                  <span className="text-xs text-muted ml-3">Order: {payment.orderNumber}</span>
                </div>
                <div className="flex items-center gap-1.5 text-xs font-medium">
                  {statusIcon(payment.status)}
                  <span>{payment.status.replace(/_/g, ' ')}</span>
                </div>
              </div>

              <div className="grid sm:grid-cols-3 gap-3 text-sm mb-4">
                <div>
                  <span className="text-muted block text-xs">Customer</span>
                  <span>{payment.customerEmail}</span>
                </div>
                <div>
                  <span className="text-muted block text-xs">Amount</span>
                  <span className="font-bold">Rs. {payment.amount.toLocaleString()}</span>
                </div>
                <div>
                  <span className="text-muted block text-xs">Date</span>
                  <span>{new Date(payment.createdAt).toLocaleDateString()}</span>
                </div>
              </div>

              {payment.receiptUrl && (
                <div className="mb-4">
                  <a href={payment.receiptUrl} target="_blank" rel="noopener noreferrer"
                    className="text-sm text-primary hover:text-primary-hover underline">
                    View Receipt →
                  </a>
                </div>
              )}

              {payment.status === 'RECEIPT_UPLOADED' && (
                <div className="flex gap-2">
                  <button
                    onClick={() => verifyPayment.mutate({ id: payment.id, approved: true, notes: 'Verified' })}
                    disabled={verifyPayment.isPending}
                    className="px-4 py-2 text-sm font-medium bg-success/10 text-success rounded-lg hover:bg-success/20 transition-all"
                  >
                    ✓ Approve
                  </button>
                  <button
                    onClick={() => {
                      const reason = prompt('Rejection reason:');
                      if (reason) verifyPayment.mutate({ id: payment.id, approved: false, notes: reason });
                    }}
                    disabled={verifyPayment.isPending}
                    className="px-4 py-2 text-sm font-medium bg-danger/10 text-danger rounded-lg hover:bg-danger/20 transition-all"
                  >
                    ✗ Reject
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
