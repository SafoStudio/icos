import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  inject,
  signal,
  computed,
  ViewChild,
  ElementRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { NgIconComponent } from '@ng-icons/core';
import { UserDocument, DOCUMENT_TYPE_OPTIONS, DocumentTypeOption } from '@models';
import { DocumentsService } from './documents.service';
import { ToastService } from '@services';

@Component({
  selector: 'app-documents-tab',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './documents-tab.component.html',
  styleUrls: ['./documents-tab.component.scss']
})
export class DocumentsTabComponent implements OnInit {
  @Input({ required: true }) userId!: string;
  @Input() editable = true;

  @Output() documentsChanged = new EventEmitter<void>();

  @ViewChild('fileInput') fileInputRef?: ElementRef<HTMLInputElement>;

  private readonly documentsService = inject(DocumentsService);
  private readonly toastService = inject(ToastService);
  private readonly sanitizer = inject(DomSanitizer);

  readonly documents = signal<UserDocument[]>([]);
  readonly loading = signal<boolean>(false);
  readonly uploading = signal<boolean>(false);
  readonly updating = signal<boolean>(false);
  readonly deletingId = signal<string | null>(null);

  readonly searchQuery = signal<string>('');
  readonly selectedTypeFilter = signal<string>('ALL');

  readonly isUploadModalOpen = signal<boolean>(false);
  readonly isEditModalOpen = signal<boolean>(false);
  readonly isPreviewModalOpen = signal<boolean>(false);
  readonly isDragging = signal<boolean>(false);

  readonly uploadFile = signal<File | null>(null);
  readonly uploadDocType = signal<string>('CONTRACT');
  readonly customUploadDocType = signal<string>('');

  readonly editingDoc = signal<UserDocument | null>(null);
  readonly editDocType = signal<string>('CONTRACT');

  readonly previewDoc = signal<UserDocument | null>(null);
  readonly previewUrl = signal<string>('');
  readonly safePreviewUrl = signal<SafeResourceUrl | null>(null);
  readonly previewMode = signal<'image' | 'pdf' | 'other'>('other');

  readonly typeOptions: DocumentTypeOption[] = DOCUMENT_TYPE_OPTIONS;

  readonly filteredDocuments = computed(() => {
    let list = this.documents();
    const filter = this.selectedTypeFilter();
    const query = this.searchQuery().trim().toLowerCase();

    if (filter !== 'ALL') {
      list = list.filter((doc) => doc.type.toUpperCase() === filter.toUpperCase());
    }

    if (query) {
      list = list.filter((doc) =>
        doc.filename.toLowerCase().includes(query) ||
        doc.type.toLowerCase().includes(query)
      );
    }

    return list;
  });

  ngOnInit() {
    if (this.userId) {
      this.loadDocuments();
    }
  }

  loadDocuments() {
    this.loading.set(true);
    this.documentsService.getUserDocuments(this.userId).subscribe({
      next: (docs) => {
        this.documents.set(docs || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching documents:', err);
        this.toastService.error('Failed to load documents');
        this.loading.set(false);
      }
    });
  }

  // --- Upload Modal ---
  openUploadModal() {
    this.uploadFile.set(null);
    this.uploadDocType.set('CONTRACT');
    this.customUploadDocType.set('');
    this.isUploadModalOpen.set(true);
  }

  closeUploadModal() {
    this.isUploadModalOpen.set(false);
    this.uploadFile.set(null);
    this.isDragging.set(false);
  }

  triggerFileInput() {
    this.fileInputRef?.nativeElement.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.uploadFile.set(input.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(false);
    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      this.uploadFile.set(event.dataTransfer.files[0]);
    }
  }

  submitUpload() {
    const file = this.uploadFile();
    if (!file) {
      this.toastService.error('Please select a file to upload');
      return;
    }

    let docType = this.uploadDocType();
    if (docType === 'CUSTOM' && this.customUploadDocType().trim()) {
      docType = this.customUploadDocType().trim().toUpperCase();
    }

    this.uploading.set(true);
    this.documentsService.uploadDocument(this.userId, docType, file).subscribe({
      next: () => {
        this.toastService.success('Document uploaded successfully');
        this.uploading.set(false);
        this.closeUploadModal();
        this.loadDocuments();
        this.documentsChanged.emit();
      },
      error: (err) => {
        console.error('Upload error:', err);
        this.toastService.error('Failed to upload document');
        this.uploading.set(false);
      }
    });
  }

  // --- Delete Document ---
  deleteDocument(doc: UserDocument) {
    if (!confirm(`Are you sure you want to delete "${doc.filename}"?`)) {
      return;
    }

    this.deletingId.set(doc.id);
    const deleteObs = doc.id
      ? this.documentsService.deleteDocument(this.userId, doc.id)
      : this.documentsService.deleteDocumentByName(this.userId, doc.type, doc.filename);

    deleteObs.subscribe({
      next: () => {
        this.toastService.success('Document deleted');
        this.deletingId.set(null);
        this.loadDocuments();
        this.documentsChanged.emit();
      },
      error: (err) => {
        console.error('Delete error:', err);
        this.toastService.error('Failed to delete document');
        this.deletingId.set(null);
      }
    });
  }

  // --- Edit Document Type ---
  openEditModal(doc: UserDocument) {
    this.editingDoc.set(doc);
    this.editDocType.set(doc.type.toUpperCase());
    this.isEditModalOpen.set(true);
  }

  closeEditModal() {
    this.isEditModalOpen.set(false);
    this.editingDoc.set(null);
  }

  submitEditType() {
    const doc = this.editingDoc();
    if (!doc || !doc.id) return;

    this.updating.set(true);
    this.documentsService.updateDocumentType(this.userId, doc.id, this.editDocType()).subscribe({
      next: () => {
        this.toastService.success('Document type updated');
        this.updating.set(false);
        this.closeEditModal();
        this.loadDocuments();
        this.documentsChanged.emit();
      },
      error: (err) => {
        console.error('Error updating document type:', err);
        this.toastService.error('Failed to update document type');
        this.updating.set(false);
      }
    });
  }

  // --- Preview & Download ---
  openPreview(doc: UserDocument) {
    this.previewDoc.set(doc);
    const url = doc.id
      ? this.documentsService.getDocumentViewUrl(this.userId, doc.id)
      : `/api/storage/document/${this.userId}/${encodeURIComponent(doc.type)}/${encodeURIComponent(doc.filename)}`;
    this.previewUrl.set(url);
    this.safePreviewUrl.set(this.sanitizer.bypassSecurityTrustResourceUrl(url));

    const ext = this.getFileExtension(doc.filename).toLowerCase();
    if (['jpg', 'jpeg', 'png', 'webp', 'svg', 'gif'].includes(ext)) {
      this.previewMode.set('image');
    } else if (ext === 'pdf') {
      this.previewMode.set('pdf');
    } else {
      this.previewMode.set('other');
    }

    this.isPreviewModalOpen.set(true);
  }

  closePreview() {
    this.isPreviewModalOpen.set(false);
    this.previewDoc.set(null);
    this.previewUrl.set('');
    this.safePreviewUrl.set(null);
  }

  openInNewTab(doc: UserDocument) {
    const url = doc.id
      ? this.documentsService.getDocumentViewUrl(this.userId, doc.id)
      : `/api/storage/document/${this.userId}/${encodeURIComponent(doc.type)}/${encodeURIComponent(doc.filename)}`;
    window.open(url, '_blank');
  }

  downloadDocument(doc: UserDocument) {
    const url = doc.id
      ? this.documentsService.getDocumentDownloadUrl(this.userId, doc.id)
      : `/api/storage/document/${this.userId}/${encodeURIComponent(doc.type)}/${encodeURIComponent(doc.filename)}`;

    const a = document.createElement('a');
    a.href = url;
    a.download = doc.filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }

  // --- Formatters & Helpers ---
  formatFileSize(bytes?: number): string {
    if (!bytes || bytes <= 0) return '0 B';
    const units = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`;
  }

  formatDate(date?: string): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  getTypeLabel(type: string): string {
    const opt = this.typeOptions.find((o) => o.value.toUpperCase() === type.toUpperCase());
    return opt ? opt.label : type;
  }

  getTypeBadgeClass(type: string): string {
    const opt = this.typeOptions.find((o) => o.value.toUpperCase() === type.toUpperCase());
    return opt ? opt.badgeClass : 'badge-other';
  }

  getFileExtension(filename: string): string {
    if (!filename || !filename.includes('.')) return '';
    return filename.substring(filename.lastIndexOf('.') + 1);
  }

  getFileIconType(filename: string): string {
    const ext = this.getFileExtension(filename).toLowerCase();
    if (['pdf'].includes(ext)) return 'pdf';
    if (['jpg', 'jpeg', 'png', 'webp', 'svg', 'gif'].includes(ext)) return 'image';
    if (['doc', 'docx', 'txt', 'rtf', 'odt'].includes(ext)) return 'doc';
    if (['xls', 'xlsx', 'csv'].includes(ext)) return 'sheet';
    if (['zip', 'rar', 'tar', 'gz', '7z'].includes(ext)) return 'archive';
    return 'file';
  }
}
