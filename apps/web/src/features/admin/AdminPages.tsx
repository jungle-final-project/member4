import { Link } from 'react-router-dom';
import { AdminShell, DataTable, MetricCard, Panel, StateMessage, StatusBadge } from '../../components/ui';
import { parts, tickets, toolRows } from '../../data/prototypeData';
import { supportRows } from '../support/SupportPages';

export function AdminDashboardPage() {
  return (
    <AdminShell title="운영 대시보드">
      <div className="grid grid-cols-4 gap-4">
        <MetricCard label="LLM Queue" value="18초" tone="orange" />
        <MetricCard label="API p95" value="420ms" tone="green" />
        <MetricCard label="AS OPEN" value="12건" tone="orange" />
        <MetricCard label="추천 성공률" value="94%" tone="green" />
      </div>
      <div className="mt-5 grid grid-cols-[1fr_420px] gap-5">
        <Panel title="최근 Agent 세션">
          <DataTable columns={['id', 'user', 'status', 'action']} rows={[
            { id: 'demo-session', user: 'user@example.com', status: <StatusBadge status="PASS" />, action: <Link className="font-bold text-brand-blue" to="/admin/agent-sessions/demo-session">상세</Link> },
            { id: 'session-1002', user: 'dev@example.com', status: <StatusBadge status="WARN" />, action: '대기' }
          ]} />
        </Panel>
        <Panel title="운영 경고">
          <StateMessage type="warn" title="가격 Job 지연" body="네이버 API mock job이 마지막 갱신 후 23시간 경과했습니다." />
        </Panel>
      </div>
    </AdminShell>
  );
}

export function AgentSessionAdminPage() {
  return (
    <AdminShell title="Agent / RAG / Tool 근거 상세">
      <div className="grid grid-cols-[1fr_520px] gap-5">
        <Panel title="Tool 호출 이력">
          <DataTable columns={['tool', 'status', 'confidence', 'summary']} rows={toolRows.map((row) => ({ ...row, status: <StatusBadge status={row.status} />, confidence: <StatusBadge status={row.confidence} /> }))} />
        </Panel>
        <Panel title="RAG Evidence">
          <DataTable columns={['source', 'summary', 'score']} rows={[
            { source: 'psu-rule-001', summary: 'GPU 피크 전력과 CPU TDP 합산 후 여유율 적용', score: '0.91' },
            { source: 'qhd-gaming-4070s', summary: 'QHD 게이밍 기준 GPU 우선 구성 근거', score: '0.84' }
          ]} />
          <div className="mt-5 rounded bg-slate-950 p-5 font-mono text-xs leading-6 text-slate-200">
            {'{'}<br />
            &nbsp;&nbsp;"status": "WARN",<br />
            &nbsp;&nbsp;"confidence": "MEDIUM",<br />
            &nbsp;&nbsp;"summary": "PSU 여유율 확인 필요"<br />
            {'}'}
          </div>
        </Panel>
      </div>
    </AdminShell>
  );
}

export function AdminPartsPage() {
  return (
    <AdminShell title="부품 / 가격 관리자">
      <div className="grid grid-cols-[1fr_360px] gap-5">
        <Panel title="부품 DB">
          <DataTable columns={['id', 'category', 'name', 'price', 'status']} rows={parts.map((part) => ({ ...part, price: `${part.price.toLocaleString()}원`, status: <StatusBadge status={part.status} /> }))} />
        </Panel>
        <Panel title="가격 Job 상태">
          <StateMessage type="info" title="목표가 비교 기준" body="배송비/쿠폰/카드할인 제외 표시 가격 기준으로 하루 1회 스냅샷을 저장합니다." />
          <button className="mt-5 w-full rounded bg-brand-blue px-4 py-3 text-sm font-bold text-white">가격 Job 실행</button>
        </Panel>
      </div>
    </AdminShell>
  );
}

export function AdminTicketsPage() {
  return (
    <AdminShell title="AS 티켓 관리자">
      <div className="grid grid-cols-[1fr_480px] gap-5">
        <Panel title="티켓 큐">
          <DataTable columns={['id', 'user', 'symptom', 'status', 'cause']} rows={supportRows} />
        </Panel>
        <Panel title="선택 티켓 상세">
          <DataTable columns={['필드', '값']} rows={[
            { 필드: 'ticketId', 값: tickets[0].id },
            { 필드: '원인 후보', 값: tickets[0].cause },
            { 필드: '최근 로그', 값: 'GPU 88도, 사용률 96%, VRAM 89%' },
            { 필드: '추천 조치', 값: '쿨링 확인 및 드라이버 재설치 안내' }
          ]} />
          <div className="mt-5 flex gap-3">
            <button className="rounded bg-brand-blue px-4 py-3 text-sm font-bold text-white">담당자 배정</button>
            <button className="rounded border border-slate-300 px-4 py-3 text-sm font-bold">상태 저장</button>
          </div>
        </Panel>
      </div>
    </AdminShell>
  );
}
