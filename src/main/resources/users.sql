INSERT INTO user (
    user_id,
    creation_date,
    email,
    first_name,
    last_modified_date,
    last_name,
    password,
    is_app_user_deactivated,
    is_update_pending,
    is_verified,
    verification_token,
    verification_token_expiration
)
VALUES
    (1, '2024-01-05 20:11:38.192771', 'projecthotel.roxfort@gmail.com', 'Harry', '2024-01-05 20:11:38.192771', 'Potter', '$2a$10$zr3JyxnPloSvyPm5URT9X.bTw8Nk7DSPLTVO0JvUGKcki95l6scBK', false, false, true, '13b16398-fc51-4005-9cbf-9d1af70ac3de', '2024-01-01 21:43:22.805000'),
    (2, '2024-01-05 20:12:00.169250', 'hermione@gmail.com', 'Hermione', '2024-01-05 22:21:53.368433', 'Granger', '$2a$10$d/z8IWldvvYsxaPsYWWqq.NmkLxBaL/GuLzf84Jb/iykm1rP9ef7C', false, false, true, '4ga563d8-ak31-4005-10bf-910af7lac3de', '2024-01-12 16:43:22.805000'),
    (3, '2024-01-05 23:12:34.226064', 'ron@gmail.com', 'Ronald', '2024-01-05 23:12:34.226064', 'Weasley', '$2a$10$1BqL6LYrR2DCQ8ZCRbOzKOhNmoB/GaVVuQUucDG/DD992hvUhxy1a', false, false, true, '98a16398-ak51-4005-9cbf-9d1af70ac3de', '2024-01-09 20:43:22.805000');