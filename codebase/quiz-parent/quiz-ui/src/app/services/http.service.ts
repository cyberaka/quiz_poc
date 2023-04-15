import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  topicId: Number = 0;
  userDet: any;
  apiURL: string = environment.baseURL;
  constructor(
    private http: HttpClient
  ) { }

  login(name: string, password: string) {
    return this.http.get(`${this.apiURL}login?userName=${name}&userPassword=${password}`);
  }

  getTopics() {
    return this.http.get(`${this.apiURL}topics`);
  }

  getSubTopics(topicId: any) {
    return this.http.get(`${this.apiURL}subtopics/${topicId}`);
  }

  getQAs(ids: any) {
    return this.http.get(`${this.apiURL}quiz/${ids.topicId}/${ids.subTopicId}/${ids.quizType}/${ids.count}`);
  }

  publishAnswers(data: any) {
    return this.http.post(`${this.apiURL}quiz/publish`, data);
  }
}
