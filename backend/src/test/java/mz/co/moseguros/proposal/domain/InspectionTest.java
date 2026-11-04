package mz.co.moseguros.proposal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.FieldValidationException;
import org.junit.jupiter.api.Test;

/** Regras da vistoria (S4-03 §3 e §4): transições, ângulos em falta, substituição e limite de extras. */
class InspectionTest {

    static final Instant NOW = Instant.parse("2027-01-06T08:15:00Z");
    static final UUID REVIEWER = UUID.randomUUID();
    static final int MAX_EXTRAS = 6;

    static Inspection pending() {
        return Inspection.pending(ProposalTest.draft(NOW, List.of(ProposalTest.line("MTPL"),
                ProposalTest.line("OD_COLLISION"))));
    }

    static InspectionPhoto.NewPhoto photo(PhotoSlot slot) {
        return new InspectionPhoto.NewPhoto(slot, UUID.randomUUID(), UUID.randomUUID(), PhotoSource.CAMERA,
                NOW.minusSeconds(60), new BigDecimal("-25.9653124"), new BigDecimal("32.583121"));
    }

    /** Acrescenta e devolve as activas, como o serviço as lê depois de gravar. */
    static List<InspectionPhoto> add(Inspection i, List<InspectionPhoto> active, PhotoSlot slot) {
        Inspection.PhotoAddition addition = i.addPhoto(active, photo(slot), NOW, MAX_EXTRAS);
        List<InspectionPhoto> next = new ArrayList<>(active.stream().filter(InspectionPhoto::isActive).toList());
        next.add(addition.added());
        return next;
    }

    static List<InspectionPhoto> allRequired(Inspection i) {
        List<InspectionPhoto> active = new ArrayList<>();
        for (PhotoSlot slot : PhotoSlot.REQUIRED) {
            active = add(i, active, slot);
        }
        return active;
    }

    static String code(Throwable t) {
        return ((DomainException) t).getCode();
    }

    @Test
    void createdPendingWithSelfPhotosOnlyWhenRequired() {
        Inspection i = pending();
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.PENDING);
        assertThat(i.getMode()).isEqualTo(InspectionMode.SELF_PHOTOS);
        assertThat(i.getRetakeSlots()).isEmpty();
        assertThatThrownBy(() -> Inspection.pending(ProposalTest.draft(NOW, List.of(ProposalTest.line("MTPL")))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void missingSlotsFollowTheSlotOrderAndAreEmptyOnSite() {
        Inspection i = pending();
        assertThat(i.missingSlots(List.of())).containsExactlyElementsOf(PhotoSlot.REQUIRED);
        List<InspectionPhoto> active = add(i, List.of(), PhotoSlot.CHASSIS);
        active = add(i, active, PhotoSlot.FRONT);
        active = add(i, active, PhotoSlot.EXTRA);
        assertThat(i.missingSlots(active)).containsExactly(PhotoSlot.REAR, PhotoSlot.LEFT, PhotoSlot.RIGHT,
                PhotoSlot.ODOMETER);
        i.requestOnSite("Matola, dias úteis de manhã", NOW);
        assertThat(i.missingSlots(active)).isEmpty();
        assertThat(Inspection.missingRequiredSlots(active)).hasSize(4);
    }

    @Test
    void aNewPhotoInAFixedSlotReplacesThePreviousOne() {
        Inspection i = pending();
        Inspection.PhotoAddition first = i.addPhoto(List.of(), photo(PhotoSlot.FRONT), NOW, MAX_EXTRAS);
        assertThat(first.replaced()).isEmpty();
        assertThat(first.added().getLatitude()).isEqualByComparingTo("-25.965312");
        Inspection.PhotoAddition second = i.addPhoto(List.of(first.added()), photo(PhotoSlot.FRONT), NOW.plusSeconds(5),
                MAX_EXTRAS);
        assertThat(second.replaced()).containsSame(first.added());
        assertThat(first.added().isActive()).isFalse();
        assertThat(first.added().getRemovedAt()).isEqualTo(NOW.plusSeconds(5));
        assertThat(second.added().isActive()).isTrue();

        // EXTRA nunca substitui
        Inspection.PhotoAddition extra1 = i.addPhoto(List.of(second.added()), photo(PhotoSlot.EXTRA), NOW, MAX_EXTRAS);
        Inspection.PhotoAddition extra2 = i.addPhoto(List.of(second.added(), extra1.added()), photo(PhotoSlot.EXTRA),
                NOW, MAX_EXTRAS);
        assertThat(extra2.replaced()).isEmpty();
        assertThat(extra1.added().isActive()).isTrue();
    }

    @Test
    void extraPhotosAreLimited() {
        Inspection i = pending();
        List<InspectionPhoto> active = new ArrayList<>();
        for (int n = 0; n < MAX_EXTRAS; n++) {
            active = add(i, active, PhotoSlot.EXTRA);
        }
        List<InspectionPhoto> six = active;
        assertThatThrownBy(() -> i.addPhoto(six, photo(PhotoSlot.EXTRA), NOW, MAX_EXTRAS))
                .isInstanceOfSatisfying(FieldValidationException.class, e -> assertThat(e.getMessage()).isNotBlank())
                .extracting(e -> ((FieldValidationException) e).getFieldCode()).isEqualTo("EXTRA_PHOTOS_LIMIT");
        // uma retirada liberta um lugar
        six.get(0).retire(NOW);
        assertThat(i.addPhoto(six, photo(PhotoSlot.EXTRA), NOW, MAX_EXTRAS).added()).isNotNull();
        // os ângulos fixos não contam
        assertThat(i.addPhoto(six, photo(PhotoSlot.FRONT), NOW, MAX_EXTRAS).added()).isNotNull();
    }

    @Test
    void submitNeedsTheSixRequiredSlots() {
        Inspection i = pending();
        List<InspectionPhoto> active = new ArrayList<>(allRequired(i));
        List<InspectionPhoto> withoutChassis = active.stream().filter(p -> p.getSlot() != PhotoSlot.CHASSIS).toList();
        assertThatThrownBy(() -> i.submit(withoutChassis, 84_500, NOW))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("INSPECTION_PHOTOS_MISSING");
                    assertThat(e.getProperties()).containsEntry("missingSlots", List.of("CHASSIS"));
                });
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.PENDING);
        i.submit(active, 84_500, NOW);
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.SUBMITTED);
        assertThat(i.getOdometerKm()).isEqualTo(84_500);
        assertThat(i.getSubmittedAt()).isEqualTo(NOW);
        // já não é editável
        assertThatThrownBy(() -> i.addPhoto(active, photo(PhotoSlot.EXTRA), NOW, MAX_EXTRAS))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INSPECTION_NOT_EDITABLE"));
        assertThatThrownBy(() -> i.submit(active, 1, NOW)).satisfies(e -> assertThat(code(e))
                .isEqualTo("INSPECTION_NOT_EDITABLE"));
        assertThatThrownBy(() -> i.requestOnSite("Maputo, sábado de manhã", NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INSPECTION_NOT_EDITABLE"));
        assertThatThrownBy(() -> i.removePhoto(active.get(0), NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INSPECTION_NOT_EDITABLE"));
    }

    @Test
    void reviewTransitions() {
        Inspection i = pending();
        List<InspectionPhoto> active = allRequired(i);
        // PENDING: o subscritor não decide
        assertThatThrownBy(() -> i.approve(active, InspectionMode.SELF_PHOTOS, null, REVIEWER, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INVALID_STATE_TRANSITION"));
        assertThatThrownBy(() -> i.reject("Fotografias pouco nítidas.", List.of(), REVIEWER, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INVALID_STATE_TRANSITION"));
        assertThatThrownBy(i::reopen).satisfies(e -> assertThat(code(e)).isEqualTo("INVALID_STATE_TRANSITION"));

        // SUBMITTED → REJECTED, com os ângulos a repetir
        i.submit(active, 84_500, NOW);
        i.reject("A fotografia do chassis está ilegível.", List.of(PhotoSlot.CHASSIS), REVIEWER, NOW.plusSeconds(60));
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.REJECTED);
        assertThat(i.getRejectionReason()).isEqualTo("A fotografia do chassis está ilegível.");
        assertThat(i.getRetakeSlots()).containsExactly(PhotoSlot.CHASSIS);
        assertThat(i.getReviewedBy()).isEqualTo(REVIEWER);
        // REJECTED: não se submete sem refazer; aprovar/recusar de novo não é permitido
        assertThatThrownBy(() -> i.submit(active, 84_500, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INVALID_STATE_TRANSITION"));
        assertThatThrownBy(() -> i.approve(active, InspectionMode.SELF_PHOTOS, null, REVIEWER, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INVALID_STATE_TRANSITION"));

        // REJECTED → PENDING com uma fotografia nova; o motivo sai, os ângulos a repetir ficam
        List<InspectionPhoto> retaken = add(i, active, PhotoSlot.CHASSIS);
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.PENDING);
        assertThat(i.getRejectionReason()).isNull();
        assertThat(i.getRetakeSlots()).containsExactly(PhotoSlot.CHASSIS);

        // PENDING → SUBMITTED limpa a revisão anterior; SUBMITTED → APPROVED
        i.submit(retaken, 84_510, NOW);
        assertThat(i.getReviewedAt()).isNull();
        assertThat(i.getRetakeSlots()).isEmpty();
        i.approve(retaken, InspectionMode.SELF_PHOTOS, "Tudo conforme.", REVIEWER, NOW);
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.APPROVED);
        assertThat(i.getReviewNotes()).isEqualTo("Tudo conforme.");
        assertThat(i.getReviewedAt()).isEqualTo(NOW);
        assertThatThrownBy(() -> i.reject("Mudei de ideias sobre isto.", List.of(), REVIEWER, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("INVALID_STATE_TRANSITION"));

        // APPROVED → PENDING (pedido de informação do S4-05)
        i.reopen();
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.PENDING);
        assertThat(i.getReviewedAt()).isNull();
        assertThat(i.getReviewNotes()).isNull();
        i.submit(retaken, 84_510, NOW);
        i.reopen(); // SUBMITTED → PENDING
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.PENDING);
    }

    @Test
    void onSiteFromPendingOrRejectedAndApprovalModes() {
        Inspection i = pending();
        List<InspectionPhoto> active = add(i, List.of(), PhotoSlot.FRONT);
        i.requestOnSite("Matola, Av. das Indústrias, dias úteis de manhã", NOW);
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.SUBMITTED);
        assertThat(i.getMode()).isEqualTo(InspectionMode.ON_SITE);
        assertThat(i.getCustomerNotes()).startsWith("Matola");
        // aprovar como fotografias exige os seis ângulos
        assertThatThrownBy(() -> i.approve(active, InspectionMode.SELF_PHOTOS, null, REVIEWER, NOW))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("INSPECTION_PHOTOS_MISSING"));
        // recusar e pedir presencial de novo a partir de REJECTED
        i.reject("Não foi possível contactar o cliente.", List.of(), REVIEWER, NOW);
        assertThat(i.getRetakeSlots()).isEmpty();
        i.requestOnSite("Maputo, Sommerschield, sábado", NOW);
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.SUBMITTED);
        assertThat(i.getRejectionReason()).isNull();
        i.approve(active, InspectionMode.ON_SITE, "Vistoria feita na Matola em 08/01.", REVIEWER, NOW);
        assertThat(i.getStatus()).isEqualTo(InspectionStatus.APPROVED);
        assertThat(i.getMode()).isEqualTo(InspectionMode.ON_SITE);

        // um cliente que enviou fotografias pode ter a vistoria aprovada como presencial
        Inspection j = pending();
        List<InspectionPhoto> all = allRequired(j);
        j.submit(all, 1000, NOW);
        j.approve(all, InspectionMode.ON_SITE, "Visita feita em 08/01.", REVIEWER, NOW);
        assertThat(j.getMode()).isEqualTo(InspectionMode.ON_SITE);
    }

    @Test
    void removePhotoOnlyWhenActiveAndOfThisInspection() {
        Inspection i = pending();
        List<InspectionPhoto> active = add(i, List.of(), PhotoSlot.FRONT);
        InspectionPhoto photo = active.get(0);
        Inspection other = pending();
        assertThatThrownBy(() -> other.removePhoto(photo, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("PHOTO_NOT_FOUND"));
        i.removePhoto(photo, NOW);
        assertThat(photo.isActive()).isFalse();
        assertThatThrownBy(() -> i.removePhoto(photo, NOW))
                .satisfies(e -> assertThat(code(e)).isEqualTo("PHOTO_NOT_FOUND"));
    }

    @Test
    void statusTable() {
        assertThat(InspectionStatus.PENDING.isEditable()).isTrue();
        assertThat(InspectionStatus.REJECTED.isEditable()).isTrue();
        assertThat(InspectionStatus.SUBMITTED.isEditable()).isFalse();
        assertThat(InspectionStatus.APPROVED.isEditable()).isFalse();
        assertThat(InspectionStatus.PENDING.canTransitionTo(InspectionStatus.APPROVED)).isFalse();
        assertThat(InspectionStatus.APPROVED.canTransitionTo(InspectionStatus.REJECTED)).isFalse();
        assertThat(PhotoSlot.parse("CHASSIS")).contains(PhotoSlot.CHASSIS);
        assertThat(PhotoSlot.parse("chassis")).isEmpty();
        assertThat(PhotoSlot.REQUIRED).hasSize(6).doesNotContain(PhotoSlot.EXTRA);
    }
}
