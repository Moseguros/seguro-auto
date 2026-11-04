package mz.co.moseguros.proposal.application;

import java.util.Comparator;
import java.util.List;

import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionPhoto;
import mz.co.moseguros.proposal.domain.PhotoSlot;

/**
 * Vistoria com as fotografias activas, para o bloco {@code inspection} (S4-03 §7).
 *
 * @param photos         activas, pela ordem de {@link PhotoSlot} e depois pela hora do envio
 * @param missingSlots   ângulos obrigatórios sem fotografia ({@code []} em {@code ON_SITE})
 * @param maxExtraPhotos {@code moseguros.proposal.inspection.max-extra-photos}
 */
public record InspectionDetails(Inspection inspection, List<InspectionPhoto> photos, List<PhotoSlot> missingSlots,
                                int maxExtraPhotos) {

    /** Ordem das fotografias na resposta. */
    static final Comparator<InspectionPhoto> ORDER = Comparator.comparing(InspectionPhoto::getSlot)
            .thenComparing(InspectionPhoto::getUploadedAt, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(InspectionPhoto::getId);

    static InspectionDetails of(Inspection inspection, List<InspectionPhoto> active, int maxExtraPhotos) {
        List<InspectionPhoto> photos = active.stream().filter(InspectionPhoto::isActive).sorted(ORDER).toList();
        return new InspectionDetails(inspection, photos, inspection.missingSlots(photos), maxExtraPhotos);
    }
}
