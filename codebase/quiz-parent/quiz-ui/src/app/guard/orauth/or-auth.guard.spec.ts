import { TestBed } from '@angular/core/testing';

import { OrAuthGuard } from './or-auth.guard';

describe('OrAuthGuard', () => {
  let guard: OrAuthGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(OrAuthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
