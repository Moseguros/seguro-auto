import {
  DndContext,
  KeyboardSensor,
  PointerSensor,
  closestCenter,
  useSensor,
  useSensors,
  type DragEndEvent,
} from '@dnd-kit/core';
import {
  SortableContext,
  sortableKeyboardCoordinates,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import ArrowDownward from '@mui/icons-material/ArrowDownward';
import ArrowUpward from '@mui/icons-material/ArrowUpward';
import DragIndicator from '@mui/icons-material/DragIndicator';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import { useTranslation } from 'react-i18next';
import { moveChannel, type OtpChannel } from './otpSettingsForm';

export interface FallbackOrderListProps {
  order: OtpChannel[];
  onChange(order: OtpChannel[]): void;
  readOnly?: boolean;
  /** Id do texto que descreve a lista. */
  labelledBy?: string;
}

/** Ordem de alternativa: arrastável (`@dnd-kit/sortable`) e com botões subir/descer. */
export function FallbackOrderList({
  order,
  onChange,
  readOnly,
  labelledBy,
}: FallbackOrderListProps) {
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );

  const onDragEnd = ({ active, over }: DragEndEvent) => {
    if (!over || active.id === over.id) return;
    const from = order.indexOf(active.id as OtpChannel);
    const to = order.indexOf(over.id as OtpChannel);
    onChange(moveChannel(order, from, to));
  };

  return (
    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={onDragEnd}>
      <SortableContext items={order} strategy={verticalListSortingStrategy}>
        <List
          aria-labelledby={labelledBy}
          data-testid="fallback-order"
          sx={{ border: 1, borderColor: 'divider', borderRadius: 1, py: 0 }}
        >
          {order.map((channel, index) => (
            <SortableChannel
              key={channel}
              channel={channel}
              index={index}
              last={index === order.length - 1}
              readOnly={readOnly}
              onMove={(to) => onChange(moveChannel(order, index, to))}
            />
          ))}
        </List>
      </SortableContext>
    </DndContext>
  );
}

function SortableChannel({
  channel,
  index,
  last,
  readOnly,
  onMove,
}: {
  channel: OtpChannel;
  index: number;
  last: boolean;
  readOnly?: boolean;
  onMove(to: number): void;
}) {
  const { t } = useTranslation();
  const { attributes, listeners, setNodeRef, setActivatorNodeRef, transform, transition } =
    useSortable({ id: channel, disabled: readOnly });
  const name = t(`channel.${channel}`);
  return (
    <ListItem
      ref={setNodeRef}
      divider={!last}
      data-channel={channel}
      sx={{
        transform: transform ? `translate3d(${transform.x}px, ${transform.y}px, 0)` : undefined,
        transition,
        bgcolor: 'background.paper',
      }}
      secondaryAction={
        readOnly ? undefined : (
          <Stack direction="row">
            <IconButton
              aria-label={t('otpSettings.moveUp', { channel: name })}
              disabled={index === 0}
              onClick={() => onMove(index - 1)}
            >
              <ArrowUpward fontSize="small" />
            </IconButton>
            <IconButton
              aria-label={t('otpSettings.moveDown', { channel: name })}
              disabled={last}
              onClick={() => onMove(index + 1)}
              edge="end"
            >
              <ArrowDownward fontSize="small" />
            </IconButton>
          </Stack>
        )
      }
    >
      {!readOnly && (
        <IconButton
          ref={setActivatorNodeRef}
          size="small"
          aria-label={t('otpSettings.drag', { channel: name })}
          sx={{ mr: 1, cursor: 'grab', touchAction: 'none' }}
          {...attributes}
          {...listeners}
        >
          <DragIndicator fontSize="small" />
        </IconButton>
      )}
      <ListItemText primary={`${index + 1}. ${name}`} />
    </ListItem>
  );
}
