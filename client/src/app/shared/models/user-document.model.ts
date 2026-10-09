export interface UserDocument {
  id: string;
  userId?: string;
  type: string;
  filename: string;
  filePath?: string;
  contentType?: string;
  fileSize?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DocumentTypeOption {
  value: string;
  label: string;
  badgeClass: string;
  icon?: string;
}

export const DOCUMENT_TYPE_OPTIONS: DocumentTypeOption[] = [
  { value: 'CONTRACT', label: 'Contract', badgeClass: 'badge-contract' },
  { value: 'IDENTIFICATION', label: 'ID / Passport', badgeClass: 'badge-id' },
  { value: 'RESUME', label: 'Resume / CV', badgeClass: 'badge-resume' },
  { value: 'DIPLOMA', label: 'Diploma / Certificate', badgeClass: 'badge-diploma' },
  { value: 'TAX', label: 'Tax Document', badgeClass: 'badge-tax' },
  { value: 'OTHER', label: 'Other', badgeClass: 'badge-other' }
];
