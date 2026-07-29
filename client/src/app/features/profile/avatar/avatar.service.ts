import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AvatarService {
  private readonly http = inject(HttpClient);

  uploadAvatar(userId: string, file: Blob | File, filename = 'avatar.png'): Observable<string> {
    const formData = new FormData();
    formData.append('file', file, filename);
    return this.http.post('/api/storage/avatar/' + userId, formData, { responseType: 'text' });
  }

  deleteAvatar(userId: string): Observable<void> {
    return this.http.delete<void>('/api/storage/avatar/' + userId);
  }

  getAvatarUrl(userId?: string, avatarPath?: string, timestamp?: number): string | null {
    if (!avatarPath || !userId) {
      return null;
    }
    if (
      avatarPath.startsWith('http://') ||
      avatarPath.startsWith('https://') ||
      avatarPath.startsWith('data:')
    ) {
      return avatarPath;
    }
    const cacheBuster = timestamp ? `?t=${timestamp}` : `?t=${Date.now()}`;
    return `/api/storage/avatar/${userId}${cacheBuster}`;
  }
}
