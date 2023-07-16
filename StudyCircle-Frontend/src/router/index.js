import { createRouter, createWebHistory } from 'vue-router'
import PostView from '../views/PostView.vue'
import CommentView from "../views/CommentView.vue"
import PersonView from '../views/PersonView.vue'
import LetterView from '../views/LetterView.vue'
import PostOverview from '../components/PostOverview.vue'
import testComp from '../components/testComp.vue'
import AIChat from '../components/AIChat.vue'
import NoticeView from '../views/NoticeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: PostView
    },
    {
      path: '/discuss',
      name: 'discuss',
      component: PostView
    },
    {
      path: '/postDetail/:postId',
      name: 'postDetail',
      component: CommentView
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
      component: AIChat
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
