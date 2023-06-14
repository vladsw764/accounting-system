INSERT INTO balance (date, balance_amount)
SELECT current_date - (n || ' days')::interval, ROUND(random()::numeric * 1000, 2)
FROM generate_series(1, 50) AS n;

