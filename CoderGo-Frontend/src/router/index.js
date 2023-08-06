import { createRouter, createWebHistory } from 'vue-router'
import ArticleView from '../views/ArticleView.vue'
import ArticleDetail from '../views/ArticleDetail.vue'
import PersonView from '../views/PersonView.vue'
import LetterView from '../views/LetterView.vue'
import testComp from '../components/testComp.vue'
import ChatView from '../views/ChatView.vue'
import NoticeView from '../views/NoticeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: ArticleView
    },
    {
      path: '/article',
      name: 'article',
      component: ArticleView
    },
    {
      path: '/articleDetail/:articleId',
      name: 'articleDetail',
      component: ArticleDetail
    },
    {
      path: '/person/:personId',
      name: 'person',
      component: PersonView
    },
    {
      path: '/letter',
      name: 'letter',
      component: LetterView
    },
    {
      path: '/chat',
      name: 'chat',
      component: ChatView
    },
    {
      path: '/notice',
      name: 'notice',
      component: NoticeView
    },
    {
      path: '/test',
      name: 'test',
      component: testComp
    }
  ]
})

export default router
