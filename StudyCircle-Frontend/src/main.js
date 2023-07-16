import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
const app = createApp(App)
// UI 设计组件
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'              // 样式
// 数据通信请求
import axios from 'axios'
axios.defaults.baseURL = 'http://localhost:9000'  // 配置 axios 请求基本 url
axios.defaults.withCredentials = true             // 配置 axios 不拦截 cookie

// 编辑框插件
import VMdEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';
import hljs from 'highlight.js';
VMdEditor.use(githubTheme, {
  Hljs: hljs,
});

import { marked } from "marked";
import { gfmHeadingId } from "marked-gfm-heading-id";
import { mangle } from "marked-mangle";
const options = {
	prefix: "my-prefix-",
};
marked.use(mangle());
marked.use(gfmHeadingId(options));


app.use(router)
app.use(ElementPlus)
app.use(VMdEditor);
app.config.globalProperties.$axios = axios;
app.mount('#app')
