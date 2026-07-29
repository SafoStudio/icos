import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef,
  ViewChild,
  inject,
  signal,
  AfterViewInit,
  OnDestroy,
  HostListener
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { AvatarService } from '../avatar.service';
import { ToastService } from '@services';

@Component({
  selector: 'app-avatar-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './avatar-modal.component.html',
  styleUrls: ['./avatar-modal.component.scss']
})
export class AvatarModalComponent implements AfterViewInit, OnDestroy {
  @Input({ required: true }) userId!: string;
  @Input() hasExistingAvatar = false;
  @Input() currentAvatarUrl: string | null = null;

  @Output() saved = new EventEmitter<void>();
  @Output() deleted = new EventEmitter<void>();
  @Output() closed = new EventEmitter<void>();

  @ViewChild('cropCanvas') cropCanvasRef?: ElementRef<HTMLCanvasElement>;
  @ViewChild('fileInput') fileInputRef?: ElementRef<HTMLInputElement>;

  private readonly avatarService = inject(AvatarService);
  private readonly toastService = inject(ToastService);

  readonly selectedFile = signal<File | null>(null);
  readonly imageLoaded = signal<boolean>(false);
  readonly zoomLevel = signal<number>(1.0);
  readonly rotation = signal<number>(0);
  readonly panX = signal<number>(0);
  readonly panY = signal<number>(0);
  readonly saving = signal<boolean>(false);
  readonly deleting = signal<boolean>(false);
  readonly isDraggingFile = signal<boolean>(false);

  private sourceImage: HTMLImageElement | null = null;
  private isPanning = false;
  private startMouseX = 0;
  private startMouseY = 0;
  private startPanX = 0;
  private startPanY = 0;
  private animFrameId: number | null = null;

  ngAfterViewInit() {
    // View initialized
  }

  ngOnDestroy() {
    if (this.animFrameId) {
      cancelAnimationFrame(this.animFrameId);
    }
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.processFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDraggingFile.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDraggingFile.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDraggingFile.set(false);

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      if (file.type.startsWith('image/')) {
        this.processFile(file);
      } else {
        this.toastService.error('Please select an image file');
      }
    }
  }

  triggerFileInput() {
    this.fileInputRef?.nativeElement.click();
  }

  private processFile(file: File) {
    this.selectedFile.set(file);
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        this.sourceImage = img;
        this.resetControls();
        this.imageLoaded.set(true);
        setTimeout(() => this.drawPreview(), 50);
      };
      img.src = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  resetControls() {
    this.zoomLevel.set(1.0);
    this.rotation.set(0);
    this.panX.set(0);
    this.panY.set(0);
  }

  centerImage() {
    this.panX.set(0);
    this.panY.set(0);
    this.drawPreview();
  }

  zoomIn() {
    const newZoom = Math.min(3.0, Number((this.zoomLevel() + 0.1).toFixed(2)));
    this.zoomLevel.set(newZoom);
    this.drawPreview();
  }

  zoomOut() {
    const newZoom = Math.max(0.5, Number((this.zoomLevel() - 0.1).toFixed(2)));
    this.zoomLevel.set(newZoom);
    this.drawPreview();
  }

  onZoomChange(value: number) {
    this.zoomLevel.set(value);
    this.drawPreview();
  }

  rotateClockwise() {
    const newRot = (this.rotation() + 90) % 360;
    this.rotation.set(newRot);
    this.drawPreview();
  }

  onWheelZoom(event: WheelEvent) {
    event.preventDefault();
    const delta = event.deltaY < 0 ? 0.05 : -0.05;
    const newZoom = Math.min(3.0, Math.max(0.5, Number((this.zoomLevel() + delta).toFixed(2))));
    this.zoomLevel.set(newZoom);
    this.drawPreview();
  }

  startPan(event: MouseEvent | TouchEvent) {
    if (!this.imageLoaded()) return;
    this.isPanning = true;

    const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX;
    const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY;

    this.startMouseX = clientX;
    this.startMouseY = clientY;
    this.startPanX = this.panX();
    this.startPanY = this.panY();
  }

  @HostListener('window:mousemove', ['$event'])
  @HostListener('window:touchmove', ['$event'])
  onPanMove(event: MouseEvent | TouchEvent) {
    if (!this.isPanning || !this.imageLoaded()) return;

    const clientX = 'touches' in event ? event.touches[0].clientX : event.clientX;
    const clientY = 'touches' in event ? event.touches[0].clientY : event.clientY;

    const dx = clientX - this.startMouseX;
    const dy = clientY - this.startMouseY;

    this.panX.set(this.startPanX + dx);
    this.panY.set(this.startPanY + dy);

    this.drawPreview();
  }

  @HostListener('window:mouseup')
  @HostListener('window:touchend')
  endPan() {
    this.isPanning = false;
  }

  drawPreview() {
    if (!this.cropCanvasRef || !this.sourceImage) return;
    const canvas = this.cropCanvasRef.nativeElement;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const size = canvas.width; // 300px viewport size
    ctx.clearRect(0, 0, size, size);

    ctx.save();

    // Move to center of canvas
    ctx.translate(size / 2 + this.panX(), size / 2 + this.panY());
    ctx.rotate((this.rotation() * Math.PI) / 180);

    const zoom = this.zoomLevel();
    const imgWidth = this.sourceImage.width;
    const imgHeight = this.sourceImage.height;

    // Calculate scaling to cover canvas cleanly at zoom 1.0
    const minScale = Math.max(size / imgWidth, size / imgHeight);
    const drawWidth = imgWidth * minScale * zoom;
    const drawHeight = imgHeight * minScale * zoom;

    ctx.drawImage(
      this.sourceImage,
      -drawWidth / 2,
      -drawHeight / 2,
      drawWidth,
      drawHeight
    );

    ctx.restore();
  }

  saveAvatar() {
    if (!this.sourceImage || !this.userId) return;

    this.saving.set(true);

    // Create export canvas at high resolution (400x400)
    const exportCanvas = document.createElement('canvas');
    exportCanvas.width = 400;
    exportCanvas.height = 400;
    const ctx = exportCanvas.getContext('2d');

    if (!ctx) {
      this.saving.set(false);
      return;
    }

    const previewCanvas = this.cropCanvasRef?.nativeElement;
    const ratio = previewCanvas ? 400 / previewCanvas.width : 1;

    ctx.save();
    ctx.translate(200 + this.panX() * ratio, 200 + this.panY() * ratio);
    ctx.rotate((this.rotation() * Math.PI) / 180);

    const zoom = this.zoomLevel();
    const imgWidth = this.sourceImage.width;
    const imgHeight = this.sourceImage.height;

    const minScale = Math.max(400 / imgWidth, 400 / imgHeight);
    const drawWidth = imgWidth * minScale * zoom;
    const drawHeight = imgHeight * minScale * zoom;

    ctx.drawImage(
      this.sourceImage,
      -drawWidth / 2,
      -drawHeight / 2,
      drawWidth,
      drawHeight
    );

    ctx.restore();

    exportCanvas.toBlob((blob) => {
      if (!blob) {
        this.toastService.error('Failed to generate image file');
        this.saving.set(false);
        return;
      }

      this.avatarService.uploadAvatar(this.userId, blob, 'avatar.png').subscribe({
        next: () => {
          this.toastService.success('Avatar updated successfully');
          this.saving.set(false);
          this.saved.emit();
          this.closeModal();
        },
        error: (err) => {
          console.error('Error uploading avatar:', err);
          this.toastService.error('Failed to upload avatar');
          this.saving.set(false);
        }
      });
    }, 'image/png', 0.95);
  }

  deleteAvatar() {
    if (!this.userId) return;

    if (!confirm('Are you sure you want to delete your avatar?')) {
      return;
    }

    this.deleting.set(true);
    this.avatarService.deleteAvatar(this.userId).subscribe({
      next: () => {
        this.toastService.success('Avatar deleted');
        this.deleting.set(false);
        this.deleted.emit();
        this.closeModal();
      },
      error: (err) => {
        console.error('Error deleting avatar:', err);
        this.toastService.error('Failed to delete avatar');
        this.deleting.set(false);
      }
    });
  }

  closeModal() {
    this.closed.emit();
  }
}
