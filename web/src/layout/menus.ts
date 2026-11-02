import type { SvgIconComponent } from '@mui/icons-material';
import AccountBalance from '@mui/icons-material/AccountBalance';
import Assignment from '@mui/icons-material/Assignment';
import BarChart from '@mui/icons-material/BarChart';
import Build from '@mui/icons-material/Build';
import Business from '@mui/icons-material/Business';
import Calculate from '@mui/icons-material/Calculate';
import CarCrash from '@mui/icons-material/CarCrash';
import Dashboard from '@mui/icons-material/Dashboard';
import Description from '@mui/icons-material/Description';
import DirectionsCar from '@mui/icons-material/DirectionsCar';
import FactCheck from '@mui/icons-material/FactCheck';
import Gavel from '@mui/icons-material/Gavel';
import Group from '@mui/icons-material/Group';
import History from '@mui/icons-material/History';
import Home from '@mui/icons-material/Home';
import Inventory from '@mui/icons-material/Inventory';
import Key from '@mui/icons-material/Key';
import ListAlt from '@mui/icons-material/ListAlt';
import LocalShipping from '@mui/icons-material/LocalShipping';
import Payments from '@mui/icons-material/Payments';
import People from '@mui/icons-material/People';
import Person from '@mui/icons-material/Person';
import PriceChange from '@mui/icons-material/PriceChange';
import ReportProblem from '@mui/icons-material/ReportProblem';
import RequestQuote from '@mui/icons-material/RequestQuote';
import Settings from '@mui/icons-material/Settings';

export const areas = [
  'cliente',
  'admin',
  'seguradora',
  'mediador',
  'perito',
  'oficina',
  'assistencia',
  'provedor',
  'supervisao',
  'frota',
] as const;

export type Area = (typeof areas)[number];

/** O texto de cada item vem da chave i18n `menu.<área>.<key>`. */
export type MenuItem = { key: string; path: string; icon: SvgIconComponent };

export const menus: Record<Area, MenuItem[]> = {
  cliente: [
    { key: 'home', path: '/cliente', icon: Home },
    { key: 'quote', path: '/cliente/simular', icon: Calculate },
    { key: 'policies', path: '/cliente/apolices', icon: Description },
    { key: 'vehicles', path: '/cliente/veiculos', icon: DirectionsCar },
    { key: 'payments', path: '/cliente/pagamentos', icon: Payments },
    { key: 'claims', path: '/cliente/sinistros', icon: CarCrash },
    { key: 'complaints', path: '/cliente/reclamacoes', icon: ReportProblem },
    { key: 'profile', path: '/cliente/perfil', icon: Person },
  ],
  admin: [
    { key: 'home', path: '/admin', icon: Dashboard },
    { key: 'organizations', path: '/admin/organizacoes', icon: Business },
    { key: 'users', path: '/admin/utilizadores', icon: Group },
    { key: 'auth', path: '/admin/autenticacao', icon: Key },
    { key: 'references', path: '/admin/referencias', icon: ListAlt },
    { key: 'audit', path: '/admin/auditoria', icon: History },
  ],
  seguradora: [
    { key: 'home', path: '/seguradora', icon: Dashboard },
    { key: 'products', path: '/seguradora/produtos', icon: Inventory },
    { key: 'tariffs', path: '/seguradora/tarifas', icon: PriceChange },
    { key: 'proposals', path: '/seguradora/propostas', icon: Assignment },
    { key: 'policies', path: '/seguradora/apolices', icon: Description },
    { key: 'payments', path: '/seguradora/pagamentos', icon: Payments },
    { key: 'claims', path: '/seguradora/sinistros', icon: CarCrash },
    { key: 'complaints', path: '/seguradora/reclamacoes', icon: ReportProblem },
    { key: 'reports', path: '/seguradora/relatorios', icon: BarChart },
    { key: 'users', path: '/seguradora/utilizadores', icon: Group },
    { key: 'settings', path: '/seguradora/configuracao', icon: Settings },
  ],
  mediador: [
    { key: 'home', path: '/mediador', icon: Dashboard },
    { key: 'customers', path: '/mediador/clientes', icon: People },
    { key: 'quotes', path: '/mediador/cotacoes', icon: Calculate },
    { key: 'policies', path: '/mediador/apolices', icon: Description },
    { key: 'reports', path: '/mediador/relatorios', icon: BarChart },
    { key: 'users', path: '/mediador/utilizadores', icon: Group },
  ],
  perito: [
    { key: 'home', path: '/perito', icon: Dashboard },
    { key: 'assessments', path: '/perito/peritagens', icon: FactCheck },
    { key: 'users', path: '/perito/utilizadores', icon: Group },
  ],
  oficina: [
    { key: 'home', path: '/oficina', icon: Dashboard },
    { key: 'repairs', path: '/oficina/reparacoes', icon: Build },
    { key: 'estimates', path: '/oficina/orcamentos', icon: RequestQuote },
    { key: 'users', path: '/oficina/utilizadores', icon: Group },
  ],
  assistencia: [
    { key: 'home', path: '/assistencia', icon: Dashboard },
    { key: 'requests', path: '/assistencia/pedidos', icon: LocalShipping },
    { key: 'users', path: '/assistencia/utilizadores', icon: Group },
  ],
  provedor: [
    { key: 'home', path: '/provedor', icon: Dashboard },
    { key: 'cases', path: '/provedor/processos', icon: Gavel },
    { key: 'users', path: '/provedor/utilizadores', icon: Group },
  ],
  supervisao: [
    { key: 'home', path: '/supervisao', icon: Dashboard },
    { key: 'insurers', path: '/supervisao/seguradoras', icon: AccountBalance },
    { key: 'reports', path: '/supervisao/relatorios', icon: BarChart },
    { key: 'users', path: '/supervisao/utilizadores', icon: Group },
  ],
  frota: [
    { key: 'home', path: '/frota', icon: Dashboard },
    { key: 'vehicles', path: '/frota/veiculos', icon: DirectionsCar },
    { key: 'policies', path: '/frota/apolices', icon: Description },
    { key: 'claims', path: '/frota/sinistros', icon: CarCrash },
    { key: 'users', path: '/frota/utilizadores', icon: Group },
  ],
};
