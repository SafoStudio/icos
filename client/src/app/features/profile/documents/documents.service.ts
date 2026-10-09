import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDocument } from '@models';

@Injectable({
  providedIn: 'root'
})
export class DocumentsService {
  private readonly http = inject(HttpClient);

  getUserDocuments(userId: string): Observable<UserDocument[]> {
    return this.http.get<UserDocument[]>(`/api/storage/documents/${userId}`);
  }

  uploadDocument(userId: string, docType: string, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post(`/api/storage/document/${userId}/${encodeURIComponent(docType)}`, formData, {
      responseType: 'text'
    });
  }

  deleteDocument(userId: string, documentId: string): Observable<void> {
    return this.http.delete<void>(`/api/storage/document/${userId}/id/${documentId}`);
  }

  deleteDocumentByName(userId: string, docType: string, filename: string): Observable<void> {
    return this.http.delete<void>(`/api/storage/document/${userId}/${encodeURIComponent(docType)}/${encodeURIComponent(filename)}`);
  }

  updateDocumentType(userId: string, documentId: string, type: string): Observable<UserDocument> {
    return this.http.patch<UserDocument>(`/api/storage/document/${userId}/${documentId}?type=${encodeURIComponent(type)}`, {});
  }

  getDocumentViewUrl(userId: string, documentId: string): string {
    return `/api/storage/document/${userId}/id/${documentId}`;
  }

  getDocumentDownloadUrl(userId: string, documentId: string): string {
    return `/api/storage/document/${userId}/download/${documentId}`;
  }
}
