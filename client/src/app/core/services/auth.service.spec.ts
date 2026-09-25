import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService, AuthResponse } from './auth.service';
import { ApiService } from './api.service';
import { User } from '@models';

describe('AuthService', () => {
  let service: AuthService;
  let apiServiceSpy: jasmine.SpyObj<ApiService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const mockUser: User = {
    id: 'user-123',
    email: 'test@icos.local',
    firstName: 'Alex',
    lastName: 'Karat',
    role: 'ADMIN',
    department: 'Engineering',
    position: 'Tech Lead',
    companyId: 'comp-1'
  };

  const mockAuthResponse: AuthResponse = {
    accessToken: 'jwt-access-token-123',
    tokenType: 'Bearer',
    expiresIn: 3600,
    user: mockUser
  };

  beforeEach(() => {
    localStorage.clear();
    const apiSpy = jasmine.createSpyObj('ApiService', ['graphql', 'get', 'post']);
    const rSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: ApiService, useValue: apiSpy },
        { provide: Router, useValue: rSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    apiServiceSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have initial unauthenticated state', () => {
    expect(service.isLoggedIn()).toBeFalse();
    expect(service.currentUser()).toBeNull();
    expect(service.token()).toBeNull();
  });

  it('should authenticate user and store token on successful login', (done) => {
    apiServiceSpy.graphql.and.returnValue(of({ login: mockAuthResponse }));

    service.login('test@icos.local', 'Password123!').subscribe((res) => {
      expect(res.user.email).toBe('test@icos.local');
      expect(service.currentUser()?.id).toBe('user-123');
      expect(service.token()).toBe('jwt-access-token-123');
      expect(service.isLoggedIn()).toBeTrue();
      expect(localStorage.getItem('token')).toBe('jwt-access-token-123');
      done();
    });
  });

  it('should clear state and navigate to login on logout', () => {
    apiServiceSpy.graphql.and.returnValue(of({ logout: true }));
    service.currentUser.set(mockUser);
    service.token.set('token');
    localStorage.setItem('token', 'token');

    service.logout();

    expect(service.currentUser()).toBeNull();
    expect(service.token()).toBeNull();
    expect(service.isLoggedIn()).toBeFalse();
    expect(localStorage.getItem('token')).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
