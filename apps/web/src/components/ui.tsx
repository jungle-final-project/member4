import { ReactNode } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { AlertTriangle, Bot, CheckCircle2, Cpu, Database, FileText, Home, LifeBuoy, LogIn, Search, Settings, Shield, UserPlus } from 'lucide-react';

type Row = Record<string, string | number | ReactNode>;

export function AppHeader() {
  return (
    <>
      <div className="h-[30px] bg-brand-navy text-xs text-slate-200">
        <div className="mx-auto flex h-full w-[1320px] items-center justify-between">
          <span>BuildGraph AI prototype · desktop only</span>
          <span>로그인 | 회원가입 | 관리자 | PC Agent</span>
        </div>
      </div>
      <header className="h-[72px] border-b border-slate-200 bg-white">
        <div className="mx-auto flex h-full w-[1320px] items-center gap-4">
          <Link to="/" className="flex items-center gap-3">
            <div className="grid h-10 w-10 place-items-center rounded bg-brand-blue text-sm font-bold text-white">BG</div>
            <div>
              <div className="text-xl font-bold leading-5 text-brand-navy">BuildGraph AI</div>
              <div className="text-xs text-slate-500">AI PC consulting platform</div>
            </div>
          </Link>
          <div className="ml-6 flex h-11 w-[560px] items-center rounded border border-slate-300 bg-slate-50 px-3">
            <Search size={17} className="text-slate-400" />
            <input className="ml-2 flex-1 bg-transparent text-sm outline-none" placeholder="예: QHD 게임용 200만원 PC" />
            <button className="rounded bg-brand-blue px-4 py-1.5 text-xs font-semibold text-white">검색</button>
          </div>
          <div className="ml-auto flex items-center gap-2">
            <HeaderButton to="/requirements/new" icon={<Bot size={15} />} label="AI 견적" />
            <HeaderButton to="/my/quotes" icon={<FileText size={15} />} label="내 견적함" />
            <HeaderButton to="/support/new" icon={<LifeBuoy size={15} />} label="AS 접수" />
            <HeaderButton to="/login" icon={<LogIn size={15} />} label="로그인" dark />
          </div>
        </div>
      </header>
      <PrimaryNav />
    </>
  );
}

function HeaderButton({ to, icon, label, dark }: { to: string; icon: ReactNode; label: string; dark?: boolean }) {
  return (
    <Link to={to} className={`flex h-9 items-center gap-1 rounded px-3 text-xs font-semibold ${dark ? 'bg-brand-navy text-white' : 'border border-slate-300 bg-white text-slate-700'}`}>
      {icon}
      {label}
    </Link>
  );
}

export function PrimaryNav() {
  const nav = [
    ['/', '홈'],
    ['/requirements/new', 'AI 견적'],
    ['/self-quote', '셀프 견적'],
    ['/builds/bg-1001', '추천 결과'],
    ['/my/quotes', '목표가 알림'],
    ['/support/new', 'AS 접수'],
    ['/admin', '관리자']
  ];
  return (
    <nav className="h-[42px] bg-brand-blue text-sm text-white">
      <div className="mx-auto flex h-full w-[1320px] items-center gap-1">
        {nav.map(([to, label]) => (
          <NavLink key={to} to={to} className={({ isActive }) => `px-6 py-3 font-semibold ${isActive ? 'bg-white/18' : 'hover:bg-white/10'}`}>
            {label}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}

export function CategorySidebar({ items }: { items: string[] }) {
  return (
    <aside className="panel w-[216px] p-4">
      <div className="mb-1 text-base font-bold">PC 카테고리</div>
      <div className="mb-4 text-xs text-slate-500">프로젝트 범위만 표시</div>
      <div className="space-y-2">
        {items.map((item, idx) => (
          <Link key={item} to={idx === 0 ? '/requirements/new' : idx === 1 ? '/self-quote' : '/'} className="block rounded border border-slate-200 bg-slate-50 px-3 py-2 text-sm hover:border-brand-blue hover:bg-brand-pale">
            {item}
          </Link>
        ))}
      </div>
    </aside>
  );
}

export function Screen({ children }: { children: ReactNode }) {
  return (
    <div className="screen-shell">
      <AppHeader />
      <main className="mx-auto w-[1320px] py-6">{children}</main>
    </div>
  );
}

export function AdminShell({ children, title }: { children: ReactNode; title: string }) {
  return (
    <div className="screen-shell flex bg-slate-100">
      <AdminSidebar />
      <div className="flex-1">
        <div className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-7">
          <div className="text-lg font-bold text-brand-navy">{title}</div>
          <div className="flex gap-2">
            <button className="rounded border border-slate-300 px-3 py-2 text-xs font-semibold">내보내기</button>
            <button className="rounded bg-brand-blue px-3 py-2 text-xs font-semibold text-white">작업 실행</button>
          </div>
        </div>
        <main className="p-7">{children}</main>
      </div>
    </div>
  );
}

function AdminSidebar() {
  const items = [
    ['/admin', 'Dashboard', Home],
    ['/admin/agent-sessions/demo-session', 'Agent/RAG', Bot],
    ['/admin/parts', 'Parts/Price', Cpu],
    ['/admin/as-tickets', 'AS Tickets', LifeBuoy]
  ] as const;
  return (
    <aside className="w-[220px] bg-brand-navy px-4 py-6 text-white">
      <div className="mb-10 text-xl font-bold">BuildGraph<br />Admin</div>
      <div className="space-y-2">
        {items.map(([to, label, Icon]) => (
          <NavLink key={to} to={to} className={({ isActive }) => `flex items-center gap-2 rounded px-3 py-2 text-sm ${isActive ? 'bg-brand-blue' : 'text-slate-300 hover:bg-white/10'}`}>
            <Icon size={16} />
            {label}
          </NavLink>
        ))}
      </div>
    </aside>
  );
}

export function Panel({ title, subtitle, children, className = '' }: { title: string; subtitle?: string; children: ReactNode; className?: string }) {
  return (
    <section className={`panel p-4 ${className}`}>
      <div className="mb-3">
        <h2 className="text-base font-bold text-slate-900">{title}</h2>
        {subtitle ? <p className="text-xs text-slate-500">{subtitle}</p> : null}
      </div>
      {children}
    </section>
  );
}

export function StatusBadge({ status }: { status: string }) {
  const s = status.toUpperCase();
  const cls = s === 'PASS' || s === 'HIGH' || s === 'ACTIVE' || s === 'RESOLVED'
    ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
    : s === 'WARN' || s === 'MEDIUM' || s === 'OPEN'
      ? 'bg-orange-50 text-orange-700 border-orange-200'
      : 'bg-slate-100 text-slate-600 border-slate-200';
  return <span className={`inline-flex rounded-full border px-2 py-1 text-[11px] font-bold ${cls}`}>{status}</span>;
}

export function MetricCard({ label, value, tone = 'blue' }: { label: string; value: string; tone?: 'blue' | 'green' | 'orange' }) {
  const color = tone === 'green' ? 'text-emerald-600' : tone === 'orange' ? 'text-orange-600' : 'text-brand-blue';
  return (
    <div className="panel min-h-[84px] p-4">
      <div className="text-xs text-slate-500">{label}</div>
      <div className={`mt-2 text-2xl font-bold ${color}`}>{value}</div>
    </div>
  );
}

export function DataTable({ columns, rows }: { columns: string[]; rows: Row[] }) {
  return (
    <div className="overflow-hidden rounded border border-slate-200">
      <table className="w-full border-collapse bg-white text-left text-xs">
        <thead className="bg-slate-100 text-slate-600">
          <tr>
            {columns.map((column) => <th key={column} className="border-b border-slate-200 px-3 py-3 font-bold">{column}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={index} className="border-b border-slate-100 last:border-0">
              {columns.map((column) => <td key={column} className="px-3 py-3 text-slate-700">{row[column]}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function QuoteCard({ build }: { build: { id: string; name: string; price: number; confidence: string; warning: string; useCase: string } }) {
  return (
    <div className="panel w-[260px] p-4">
      <div className="mb-3 h-24 rounded bg-gradient-to-br from-slate-100 to-brand-pale" />
      <div className="font-bold">{build.name}</div>
      <div className="mt-1 text-xs text-slate-500">{build.useCase}</div>
      <div className="mt-3 text-2xl font-bold text-brand-blue">{build.price.toLocaleString()}원</div>
      <div className="mt-3 flex items-center gap-2">
        <StatusBadge status={build.confidence} />
        <span className="text-xs text-orange-600">{build.warning}</span>
      </div>
      <div className="mt-4 flex gap-2">
        <Link to={`/builds/${build.id}`} className="rounded bg-brand-blue px-3 py-2 text-xs font-semibold text-white">상세 보기</Link>
        <Link to={`/builds/${build.id}/change-part`} className="rounded border border-slate-300 px-3 py-2 text-xs font-semibold">부품 변경</Link>
      </div>
    </div>
  );
}

export function StateMessage({ type, title, body }: { type: 'info' | 'warn' | 'success'; title: string; body: string }) {
  const icon = type === 'success' ? <CheckCircle2 size={18} /> : type === 'warn' ? <AlertTriangle size={18} /> : <Shield size={18} />;
  const cls = type === 'success' ? 'border-emerald-200 bg-emerald-50 text-emerald-800' : type === 'warn' ? 'border-orange-200 bg-orange-50 text-orange-800' : 'border-blue-200 bg-blue-50 text-blue-800';
  return (
    <div className={`flex items-start gap-3 rounded border p-4 ${cls}`}>
      {icon}
      <div>
        <div className="font-bold">{title}</div>
        <div className="mt-1 text-xs leading-5">{body}</div>
      </div>
    </div>
  );
}

export const Icons = { UserPlus, Database, Settings };
