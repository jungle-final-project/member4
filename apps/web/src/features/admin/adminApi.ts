import { api } from '../../lib/api';

export type AdminDashboard = {
  agentRunning: number;
  openTickets: number;
  priceJobsRunning: number;
  degraded: boolean;
  generatedAt?: string;
};

export type AdminAuditLog = {
  action?: string;
  targetType?: string;
  targetId?: string | null;
  metadata?: Record<string, unknown> | null;
  createdAt?: string;
};

export type AdminAuditLogsResponse = {
  items: AdminAuditLog[];
};

export function getAdminDashboard() {
  return api<AdminDashboard>('/api/admin/dashboard');
}

export function getRecentAdminAuditLogs() {
  return api<AdminAuditLogsResponse>('/api/admin/audit-logs/recent');
}

export function getAgentSession(sessionId: string) {
  return api(`/api/admin/agent-sessions/${sessionId}`);
}

export function getToolInvocation(invocationId: string) {
  return api(`/api/admin/tool-invocations/${invocationId}`);
}

export function getRagEvidence(evidenceId: string) {
  return api(`/api/admin/rag-evidence/${evidenceId}`);
}

export function getAdminTicket(ticketId: string) {
  return api(`/api/admin/as-tickets/${ticketId}`);
}

export function runPriceJob() {
  return api('/api/admin/price-jobs/run', { method: 'POST' });
}
