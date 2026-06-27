import { FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Bot, FileText, Plus, Save } from 'lucide-react';
import { CategorySidebar, DataTable, MetricCard, Panel, QuoteCard, Screen, StateMessage, StatusBadge } from '../../components/ui';
import { builds, categories, parts, toolRows } from '../../data/prototypeData';

export function HomePage() {
  return (
    <Screen>
      <div className="mb-3 text-xs text-slate-500">Home / AI PC consulting</div>
      <div className="grid grid-cols-[216px_1fr_388px] gap-5">
        <CategorySidebar items={categories} />
        <div className="space-y-5">
          <Panel title="AI 기반 온라인 견적" subtitle="용도와 예산을 입력하면 요구사항을 구조화하고 추천 Build를 생성합니다.">
            <div className="grid grid-cols-[1fr_190px] gap-4">
              <textarea className="h-24 rounded border border-slate-300 p-3 text-sm outline-none focus:border-brand-blue" defaultValue="200만원 안에서 QHD 게임과 개발을 같이 할 PC 추천해줘. NVIDIA 선호." />
              <div className="space-y-3">
                <Link to="/requirements/new" className="flex h-10 items-center justify-center gap-2 rounded bg-brand-blue text-sm font-bold text-white"><Bot size={16} /> AI 견적 생성</Link>
                <Link to="/self-quote" className="flex h-10 items-center justify-center rounded border border-slate-300 text-sm font-bold">셀프 견적 보기</Link>
                <div className="rounded bg-brand-pale px-3 py-2 text-xs font-semibold text-brand-blue">POST /api/requirements/parse</div>
              </div>
            </div>
          </Panel>
          <Panel title="추천 Build 샘플" subtitle="P0 범위: 구매 전 AI 컨설팅, Tool 검증, 목표가 알림">
            <div className="flex gap-4">
              {builds.map((build) => <QuoteCard key={build.id} build={build} />)}
            </div>
          </Panel>
          <Panel title="운영 공지 / 작업 큐">
            <DataTable columns={['구분', '내용', '상태']} rows={[
              { 구분: 'Tool', 내용: '호환성/전력/규격 검증 seed 응답 연결', 상태: <StatusBadge status="PASS" /> },
              { 구분: 'LLM', 내용: '요구사항 파싱은 fallback mock 우선', 상태: <StatusBadge status="WARN" /> },
              { 구분: 'Agent', 내용: '최근 30분 JSONL 업로드 흐름 준비', 상태: <StatusBadge status="PASS" /> }
            ]} />
          </Panel>
        </div>
        <Panel title="빠른 로그인" subtitle="프로토타입 JWT 흐름">
          <div className="space-y-3">
            <input className="h-10 w-full rounded border border-slate-300 px-3 text-sm" defaultValue="user@example.com" />
            <Link to="/login" className="block rounded bg-brand-blue px-4 py-2 text-center text-sm font-bold text-white">로그인</Link>
            <Link to="/signup" className="block rounded border border-slate-300 px-4 py-2 text-center text-sm font-bold">회원가입</Link>
            <StateMessage type="info" title="MVP 제외" body="결제/배송/자체 원격제어는 이 프로토타입 범위에서 제외합니다." />
          </div>
        </Panel>
      </div>
    </Screen>
  );
}

export function RequirementPage() {
  const navigate = useNavigate();
  function submit(event: FormEvent) {
    event.preventDefault();
    navigate('/builds/bg-1001');
  }
  return (
    <Screen>
      <div className="grid grid-cols-[650px_1fr_300px] gap-5">
        <Panel title="AI 견적 입력" subtitle="자연어 또는 필수 필드 중 하나 이상 입력">
          <form onSubmit={submit} className="space-y-5">
            <textarea className="h-36 w-full rounded border border-slate-300 p-4 text-sm" defaultValue="배틀그라운드 QHD 옵션, 개발 IDE도 같이 쓸 예정. 예산은 200만원." />
            <div className="grid grid-cols-3 gap-4">
              <Field label="예산" value="2,000,000" />
              <Field label="주 용도" value="게임, 개발" />
              <Field label="해상도" value="QHD" />
              <Field label="브랜드 선호" value="NVIDIA" />
              <Field label="우선순위" value="성능 > 안정성 > 가격" wide />
            </div>
            <div className="flex gap-2">
              {['게임', '개발', '영상편집', 'AI 실습', '저소음', '업그레이드 여유'].map((chip) => <span key={chip} className="rounded-full border border-blue-200 bg-brand-pale px-3 py-1 text-xs font-bold text-brand-blue">{chip}</span>)}
            </div>
            <div className="flex gap-3">
              <button className="rounded bg-brand-blue px-5 py-2 text-sm font-bold text-white">요구사항 분석</button>
              <button type="button" className="rounded border border-slate-300 px-5 py-2 text-sm font-bold">초기화</button>
            </div>
          </form>
        </Panel>
        <Panel title="파싱 결과" subtitle="LLM fallback 결과">
          <DataTable columns={['필드', '값', '확신도']} rows={[
            { 필드: 'budget', 값: '2,000,000', 확신도: <StatusBadge status="HIGH" /> },
            { 필드: 'usage', 값: 'gaming, development', 확신도: <StatusBadge status="HIGH" /> },
            { 필드: 'targetResolution', 값: 'QHD', 확신도: <StatusBadge status="MEDIUM" /> },
            { 필드: 'brandPreference', 값: 'NVIDIA', 확신도: <StatusBadge status="HIGH" /> }
          ]} />
        </Panel>
        <Panel title="추가 질문" subtitle="누락 필드가 있을 때 표시">
          <StateMessage type="success" title="추가 질문 없음" body="추천 생성에 필요한 기본 조건이 충족되었습니다." />
          <Link to="/builds/bg-1001" className="mt-5 block rounded bg-brand-blue px-4 py-3 text-center text-sm font-bold text-white">추천 결과 보기</Link>
        </Panel>
      </div>
    </Screen>
  );
}

function Field({ label, value, wide }: { label: string; value: string; wide?: boolean }) {
  return (
    <label className={wide ? 'col-span-2' : ''}>
      <div className="mb-1 text-xs font-bold text-slate-500">{label}</div>
      <input className="h-10 w-full rounded border border-slate-300 px-3 text-sm" defaultValue={value} />
    </label>
  );
}

export function BuildResultPage() {
  const { buildId = 'bg-1001' } = useParams();
  return (
    <Screen>
      <div className="grid grid-cols-[1fr_320px] gap-5">
        <div className="space-y-5">
          <Panel title={`추천 Build 결과 / ${buildId}`} subtitle="추천 Build 2~3개, Tool 검증 근거, 경고를 함께 표시">
            <div className="flex gap-4">
              {builds.map((build) => <QuoteCard key={build.id} build={build} />)}
            </div>
          </Panel>
          <Panel title="Tool 검증 결과">
            <DataTable columns={['tool', 'status', 'confidence', 'summary']} rows={toolRows.map((row) => ({ ...row, status: <StatusBadge status={row.status} />, confidence: <StatusBadge status={row.confidence} /> }))} />
          </Panel>
        </div>
        <Panel title="견적 요약 / 액션">
          <div className="space-y-4">
            <MetricCard label="총액" value="1,980,000원" />
            <StateMessage type="warn" title="PSU 여유율 확인" body="피크 전력 기준 여유율이 낮아 750W 이상을 권장합니다." />
            <Link to={`/builds/${buildId}/change-part`} className="block rounded bg-brand-blue px-4 py-3 text-center text-sm font-bold text-white">부품 변경 비교</Link>
            <Link to="/my/quotes" className="block rounded border border-slate-300 px-4 py-3 text-center text-sm font-bold">견적 저장</Link>
          </div>
        </Panel>
      </div>
    </Screen>
  );
}

export function SelfQuotePage() {
  return (
    <Screen>
      <div className="grid grid-cols-[216px_1fr_300px] gap-5">
        <CategorySidebar items={categories} />
        <Panel title="셀프 견적 / 부품 선택표" subtitle="필수 부품 선택 후 Tool 검증을 실행합니다.">
          <DataTable columns={['category', 'name', 'price', 'status', 'score']} rows={parts.map((part) => ({ ...part, price: `${part.price.toLocaleString()}원`, status: <StatusBadge status={part.status} /> }))} />
        </Panel>
        <Panel title="검증 / 합계">
          <MetricCard label="선택 합계" value="1,581,000원" />
          <div className="mt-4 space-y-3">
            <button className="w-full rounded bg-brand-blue px-4 py-3 text-sm font-bold text-white">Tool 검증하기</button>
            <Link to="/builds/bg-1001" className="block rounded border border-slate-300 px-4 py-3 text-center text-sm font-bold">추천 결과로 보기</Link>
          </div>
        </Panel>
      </div>
    </Screen>
  );
}

export function ChangePartPage() {
  const { buildId = 'bg-1001' } = useParams();
  return (
    <Screen>
      <div className="grid grid-cols-[1fr_360px] gap-5">
        <Panel title="부품 변경 비교 / 검증" subtitle={`Build ${buildId}: 변경 전후 가격, 성능, 전력, 호환성 차이`}>
          <DataTable columns={['구분', '변경 전', '변경 후', '차이', '판정']} rows={[
            { 구분: 'GPU', '변경 전': 'RTX 4060 Ti', '변경 후': 'RTX 4070 SUPER', 차이: '+318,000원', 판정: <StatusBadge status="WARN" /> },
            { 구분: 'QHD 성능', '변경 전': '기준 1.0x', '변경 후': '예상 1.42x', 차이: '+42%', 판정: <StatusBadge status="PASS" /> },
            { 구분: '전력', '변경 전': '520W peak', '변경 후': '640W peak', 차이: '+120W', 판정: <StatusBadge status="WARN" /> }
          ]} />
        </Panel>
        <Panel title="적용 결과">
          <StateMessage type="warn" title="조건부 적용 가능" body="성능은 개선되지만 PSU 여유율이 낮습니다. 750W 이상 구성과 함께 적용하세요." />
          <div className="mt-5 space-y-3">
            <Link to={`/builds/${buildId}`} className="block rounded bg-brand-blue px-4 py-3 text-center text-sm font-bold text-white">변경 적용</Link>
            <Link to="/self-quote" className="block rounded border border-slate-300 px-4 py-3 text-center text-sm font-bold">다른 부품 선택</Link>
          </div>
        </Panel>
      </div>
    </Screen>
  );
}

export function MyQuotesPage() {
  return (
    <Screen>
      <div className="grid grid-cols-[1fr_340px] gap-5">
        <Panel title="내 견적함 / 목표가 알림" subtitle="저장 Build와 부품별 목표가 도달 알림을 관리합니다.">
          <DataTable columns={['id', 'name', 'price', 'confidence', 'action']} rows={builds.map((build) => ({ ...build, price: `${build.price.toLocaleString()}원`, confidence: <StatusBadge status={build.confidence} />, action: <Link to={`/builds/${build.id}`} className="font-bold text-brand-blue">상세</Link> }))} />
        </Panel>
        <Panel title="목표가 알림 등록">
          <div className="space-y-3">
            <Field label="부품 ID" value="gpu-4070s" />
            <Field label="목표가" value="850,000" />
            <Field label="이메일" value="user@example.com" />
            <button className="w-full rounded bg-brand-blue px-4 py-3 text-sm font-bold text-white"><Save className="mr-1 inline" size={15} /> 알림 등록</button>
          </div>
        </Panel>
      </div>
    </Screen>
  );
}
