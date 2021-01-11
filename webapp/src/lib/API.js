import axios from "axios"

const API = function (Vue) {
  this._cache = []

  const fetch = (entity, id, queryParams ) => {
    return axios.get(`/${entity}${(id ? `/${id}` : "")}`, {
      params: queryParams
    })
  }

  this.fetchAudiobooks = (searchTerm, unabridgedOnly, page) => {
    fetch()
  }

  this.fetchData = () => {
    fetch()
  }

  this.fetchAuthors = () => {

  }
}

API.install = function (Vue) {
  Vue.prototype.$api = new API(Vue)
}

export default API