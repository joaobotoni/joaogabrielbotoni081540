import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/pages/main/app.config';
import { App } from './app/pages/main/app';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
