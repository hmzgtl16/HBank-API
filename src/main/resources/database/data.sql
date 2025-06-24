insert into public.table_privilege (privilege_id, privilege_name)
values  ('3223214c-83e1-4914-a185-539975781a03', 'read:user'),
        ('8866583a-992e-405d-949a-5681b5718c02', 'write:user'),
        ('8739111d-1925-4402-909d-5162b329281b', 'read:customer'),
        ('41839466-582b-492f-8904-451515862909', 'write:customer'),
        ('963111b6-243c-4946-b193-7062f0753659', 'read:account'),
        ('682a3399-d243-4358-b247-04699103536e', 'write:account'),
        ('76a788a2-6234-482a-9581-979116020627', 'read:transaction'),
        ('2038126c-552b-4062-b31e-148332731675', 'write:transaction')
on conflict do nothing;

insert into public.table_role (role_id, role_name)
values  ('1717a14d-4d89-4b27-ef15-5b8aac1112fc', 'ROLE::CUSTOMER'),
        ('2828b25e-5e9a-4c38-f026-6c9bbd22230d', 'ROLE::USER')
on conflict do nothing;

insert into public.table_role_privilege (privilege_id, role_id)
values  ('3223214c-83e1-4914-a185-539975781a03', '2828b25e-5e9a-4c38-f026-6c9bbd22230d'),
        ('8866583a-992e-405d-949a-5681b5718c02', '2828b25e-5e9a-4c38-f026-6c9bbd22230d'),
        ('8739111d-1925-4402-909d-5162b329281b', '1717a14d-4d89-4b27-ef15-5b8aac1112fc'),
        ('41839466-582b-492f-8904-451515862909', '1717a14d-4d89-4b27-ef15-5b8aac1112fc'),
        ('963111b6-243c-4946-b193-7062f0753659', '1717a14d-4d89-4b27-ef15-5b8aac1112fc'),
        ('682a3399-d243-4358-b247-04699103536e', '1717a14d-4d89-4b27-ef15-5b8aac1112fc'),
        ('76a788a2-6234-482a-9581-979116020627', '1717a14d-4d89-4b27-ef15-5b8aac1112fc'),
        ('2038126c-552b-4062-b31e-148332731675', '1717a14d-4d89-4b27-ef15-5b8aac1112fc')
on conflict do nothing;