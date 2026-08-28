export type EntityType =
  | 'USER'
  | 'COMPANY'
  | 'DEPARTMENT'
  | 'POSITION'
  | 'JOB'
  | 'CANDIDATE'
  | 'ONBOARDING';

export type FieldType =
  | 'STRING'
  | 'MULTILINE'
  | 'NUMBER'
  | 'DATE'
  | 'BOOLEAN'
  | 'ENUM'
  | 'MULTI_SELECT'
  | 'URL'
  | 'ENTITY_REF';

export interface CustomFieldDefinition {
  id: string;
  entityType: EntityType;
  key: string;
  label: string;
  fieldType: FieldType;
  refEntityType?: string | null;
  required: boolean;
  filterable: boolean;
  options?: string[] | null;
  description?: string | null;
  placeholder?: string | null;
  defaultValue?: string | null;
  validationRegex?: string | null;
  validationMessage?: string | null;
  minValue?: number | null;
  maxValue?: number | null;
  conditionalFieldKey?: string | null;
  conditionalValue?: string | null;
  orderIndex: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CustomFieldValue {
  id: string;
  entityType: EntityType;
  entityId: string;
  definition: CustomFieldDefinition;
  valueText?: string | null;
  valueNumber?: number | null;
  valueBool?: boolean | null;
  valueDate?: string | null;
  valueEntityId?: string | null;
  valueEntityType?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface CustomFieldDefinitionInput {
  entityType: EntityType;
  key: string;
  label: string;
  fieldType: FieldType;
  refEntityType?: string | null;
  required?: boolean;
  filterable?: boolean;
  options?: string[] | null;
  description?: string | null;
  placeholder?: string | null;
  defaultValue?: string | null;
  validationRegex?: string | null;
  validationMessage?: string | null;
  minValue?: number | null;
  maxValue?: number | null;
  conditionalFieldKey?: string | null;
  conditionalValue?: string | null;
  orderIndex?: number;
}

export interface CustomFieldValueInput {
  entityType: EntityType;
  entityId: string;
  fieldKey: string;
  value?: any;
}

export interface FieldTypeMeta {
  type: FieldType;
  label: string;
  description: string;
  icon: string;
  category: 'text' | 'choice' | 'numeric' | 'special';
}

export const FIELD_TYPES_INFO: Record<FieldType, FieldTypeMeta> = {
  STRING: {
    type: 'STRING',
    label: 'Text (Short)',
    description: 'Single-line text input for names, cities, short codes',
    icon: 'tablerTypography',
    category: 'text'
  },
  MULTILINE: {
    type: 'MULTILINE',
    label: 'Multiline Text',
    description: 'Textarea for detailed notes, descriptions, comments',
    icon: 'tablerAlignLeft',
    category: 'text'
  },
  NUMBER: {
    type: 'NUMBER',
    label: 'Number',
    description: 'Numeric value for salaries, years of experience, scores',
    icon: 'tablerNumbers',
    category: 'numeric'
  },
  DATE: {
    type: 'DATE',
    label: 'Date',
    description: 'Calendar date picker for start dates, deadlines',
    icon: 'tablerCalendar',
    category: 'special'
  },
  BOOLEAN: {
    type: 'BOOLEAN',
    label: 'Yes / No (Boolean)',
    description: 'Toggle switch for agreements, permissions, flags',
    icon: 'tablerToggleRight',
    category: 'choice'
  },
  ENUM: {
    type: 'ENUM',
    label: 'Single Select',
    description: 'Dropdown list with predefined options (single choice)',
    icon: 'tablerList',
    category: 'choice'
  },
  MULTI_SELECT: {
    type: 'MULTI_SELECT',
    label: 'Multi Select',
    description: 'Multiple selection chips from predefined options',
    icon: 'tablerListCheck',
    category: 'choice'
  },
  URL: {
    type: 'URL',
    label: 'Web Link / URL',
    description: 'Validated URL input with direct preview link',
    icon: 'tablerLink',
    category: 'special'
  },
  ENTITY_REF: {
    type: 'ENTITY_REF',
    label: 'Entity Reference',
    description: 'Reference link to another record or user',
    icon: 'tablerId',
    category: 'special'
  }
};

export const ENTITY_TYPES_INFO: Record<EntityType, { label: string; icon: string; description: string }> = {
  CANDIDATE: {
    label: 'Candidate',
    icon: 'tablerUserCheck',
    description: 'Recruitment applicants, visa status, portfolio links, relocation'
  },
  JOB: {
    label: 'Job Vacancy',
    icon: 'tablerBriefcase',
    description: 'Vacancies, department codes, budget allocations, hiring priority'
  },
  USER: {
    label: 'User / Employee',
    icon: 'tablerUser',
    description: 'Employee profiles, shirt sizes, equipment IDs, languages'
  },
  ONBOARDING: {
    label: 'Onboarding',
    icon: 'tablerClipboardList',
    description: 'Onboarding flows, checklist steps, mentor notes, equipment delivery'
  },
  DEPARTMENT: {
    label: 'Department',
    icon: 'tablerBuilding',
    description: 'Organizational departments, cost centers, floor locations'
  },
  POSITION: {
    label: 'Position / Role',
    icon: 'tablerId',
    description: 'Job titles, grade levels, required certifications'
  },
  COMPANY: {
    label: 'Company',
    icon: 'tablerBuildingCommunity',
    description: 'Global company settings, policy links, legal entities'
  }
};
