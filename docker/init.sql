-- Create sequence only if it doesn't exist
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'task_sequence') THEN
CREATE SEQUENCE task_sequence
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;
END IF;
END
$$;

-- Create table only if it doesn't exist
CREATE TABLE IF NOT EXISTS task (
                                  id BIGINT PRIMARY KEY DEFAULT nextval('task_sequence'),
  title VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  status VARCHAR(255) NOT NULL,
  due_date DATE NOT NULL
  );

-- Insert data only if table is empty
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM task) THEN
    INSERT INTO task (id, title, description, status, due_date) VALUES
      (nextval('task_sequence'), 'Schedule team meeting', 'Organise a 30-minute sync with the operations team for next week', 'PENDING', '2025-06-24'),
      (nextval('task_sequence'), 'Prepare monthly report', 'Compile performance metrics and budget summary for June', 'IN_PROGRESS', '2025-06-27'),
      (nextval('task_sequence'), 'Update contact list', 'Ensure all staff phone numbers and emails are up-to-date', 'PENDING', '2025-06-25'),
      (nextval('task_sequence'), 'Book conference room', 'Reserve Room 2B for the finance presentation on Friday', 'COMPLETED', '2025-06-20'),
      (nextval('task_sequence'), 'Scan and archive invoices', 'Digitise and file all incoming invoices from this month', 'PENDING', '2025-06-26'),
      (nextval('task_sequence'), 'Order office supplies', 'Restock printer paper, pens, and sticky notes', 'IN_PROGRESS', '2025-06-28'),
      (nextval('task_sequence'), 'Send calendar invites', 'Invite attendees to the quarterly planning workshop', 'COMPLETED', '2025-06-19'),
      (nextval('task_sequence'), 'Follow up on vendor payment', 'Check with finance if last week’s payment has been processed', 'IN_PROGRESS', '2025-06-21'),
      (nextval('task_sequence'), 'Prepare onboarding pack', 'Print welcome materials and ID badges for new starters', 'PENDING', '2025-06-29'),
      (nextval('task_sequence'), 'Archive old emails', 'Move all emails older than 2023 into the archive folder', 'COMPLETED', '2025-06-15');
END IF;
END
$$;
