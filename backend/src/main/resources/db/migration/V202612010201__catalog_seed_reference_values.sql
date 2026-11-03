-- Valores iniciais das tabelas de referência (S2-02 §3), em todos os ambientes.
-- O administrador completa e corrige as listas em /admin/referencias; esta migração não se edita.

insert into catalog.plate_format (id, created_at, created_by, updated_at, updated_by, code, label, pattern,
                                  display_template, example, sort_order)
values
  (gen_random_uuid(), now(), 'system', now(), 'system', 'STANDARD', 'Actual (ABC-123-MC)',
   '^(?<l>[A-Z]{3})(?<n>\d{3})(?<p>[A-Z]{2})$', '{l}-{n}-{p}', 'ABC-123-MC', 10),
  (gen_random_uuid(), now(), 'system', now(), 'system', 'LEGACY', 'Antigo (ABC-12-34)',
   '^(?<l>[A-Z]{3})(?<a>\d{2})(?<b>\d{2})$', '{l}-{a}-{b}', 'ABC-12-34', 20);

-- Valor provisório (ver "Questões" da ficha S2-02): 3 000 000,00 MT em centavos.
insert into catalog.platform_setting (id, created_at, created_by, updated_at, updated_by, key, value_type, value)
values (gen_random_uuid(), now(), 'system', now(), 'system', 'MTPL_MIN_CAPITAL', 'MONEY', '300000000');

insert into catalog.reference_value (id, created_at, created_by, updated_at, updated_by, domain, code, label_pt,
                                     parent_code, sort_order)
select gen_random_uuid(), now(), 'system', now(), 'system', 'VEHICLE_MAKE', v.code, v.label, null, 100
  from (values
    ('TOYOTA', 'Toyota'),
    ('NISSAN', 'Nissan'),
    ('MITSUBISHI', 'Mitsubishi'),
    ('MAZDA', 'Mazda'),
    ('HONDA', 'Honda'),
    ('HYUNDAI', 'Hyundai'),
    ('KIA', 'Kia'),
    ('ISUZU', 'Isuzu'),
    ('FORD', 'Ford'),
    ('VOLKSWAGEN', 'Volkswagen'),
    ('MERCEDES_BENZ', 'Mercedes-Benz'),
    ('BMW', 'BMW'),
    ('SUZUKI', 'Suzuki'),
    ('LAND_ROVER', 'Land Rover'),
    ('RENAULT', 'Renault'),
    ('PEUGEOT', 'Peugeot'),
    ('CHEVROLET', 'Chevrolet'),
    ('SCANIA', 'Scania'),
    ('MAN', 'MAN'),
    ('VOLVO', 'Volvo'),
    ('YAMAHA', 'Yamaha'),
    ('LIFAN', 'Lifan')
  ) as v (code, label);

insert into catalog.reference_value (id, created_at, created_by, updated_at, updated_by, domain, code, label_pt,
                                     parent_code, sort_order)
select gen_random_uuid(), now(), 'system', now(), 'system', 'VEHICLE_MODEL', v.code, v.label, v.parent, 100
  from (values
    ('TOYOTA_COROLLA', 'Corolla', 'TOYOTA'),
    ('TOYOTA_HILUX', 'Hilux', 'TOYOTA'),
    ('TOYOTA_RAV4', 'RAV4', 'TOYOTA'),
    ('TOYOTA_LAND_CRUISER', 'Land Cruiser', 'TOYOTA'),
    ('TOYOTA_HIACE', 'Hiace', 'TOYOTA'),
    ('TOYOTA_VITZ', 'Vitz', 'TOYOTA'),
    ('TOYOTA_RUNX', 'Runx', 'TOYOTA'),
    ('TOYOTA_FORTUNER', 'Fortuner', 'TOYOTA'),
    ('TOYOTA_QUANTUM', 'Quantum', 'TOYOTA'),
    ('NISSAN_HARDBODY', 'Hardbody', 'NISSAN'),
    ('NISSAN_NP200', 'NP200', 'NISSAN'),
    ('NISSAN_NAVARA', 'Navara', 'NISSAN'),
    ('NISSAN_X_TRAIL', 'X-Trail', 'NISSAN'),
    ('MITSUBISHI_PAJERO', 'Pajero', 'MITSUBISHI'),
    ('MITSUBISHI_L200', 'L200', 'MITSUBISHI'),
    ('ISUZU_D_MAX', 'D-Max', 'ISUZU'),
    ('ISUZU_KB', 'KB', 'ISUZU'),
    ('FORD_RANGER', 'Ranger', 'FORD'),
    ('VOLKSWAGEN_POLO', 'Polo', 'VOLKSWAGEN'),
    ('VOLKSWAGEN_GOLF', 'Golf', 'VOLKSWAGEN'),
    ('MERCEDES_BENZ_ACTROS', 'Actros', 'MERCEDES_BENZ'),
    ('HONDA_CG_125', 'CG 125', 'HONDA')
  ) as v (code, label, parent);

-- Lista provisória (ver "Questões" da ficha S2-02); ordem pela lista.
insert into catalog.reference_value (id, created_at, created_by, updated_at, updated_by, domain, code, label_pt,
                                     parent_code, sort_order)
select gen_random_uuid(), now(), 'system', now(), 'system', 'LICENCE_CATEGORY', v.code, v.label, null, v.ord
  from (values
    ('A1', 'A1 — Motociclos ligeiros', 10),
    ('A', 'A — Motociclos', 20),
    ('B', 'B — Ligeiros', 30),
    ('C1', 'C1 — Pesados de mercadorias até 7 500 kg', 40),
    ('C', 'C — Pesados de mercadorias', 50),
    ('D1', 'D1 — Pesados de passageiros até 16 lugares', 60),
    ('D', 'D — Pesados de passageiros', 70),
    ('BE', 'BE — Ligeiros com reboque', 80),
    ('CE', 'CE — Pesados com reboque', 90),
    ('DE', 'DE — Passageiros com reboque', 100)
  ) as v (code, label, ord);
