import Vue from 'vue'
import Router from 'vue-router'
import Help from '@/pages/help/index'
import Portfolios from '@/pages/portfolio/index'
import Portfolio from '@/pages/portfolio/show'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'portfolios',
      component: Portfolios
    },
    {
      path: '/portfolio/:id',
      name: 'portfolio',
      component: Portfolio
    },
    {
      path: '/help',
      name: 'help',
      component: Help
    }
  ]
})
