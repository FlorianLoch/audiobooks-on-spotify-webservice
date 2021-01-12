import axios from "axios"

const PAGE_SIZE = 10

const API = function () {
  this._cache = []

  const fetch = (entity, params, page) => {
    params = params || {};
    params.offset = page * PAGE_SIZE;
    params.limit = PAGE_SIZE

    const prom = axios.get(`/${entity}`, { params })

    return prom.then((response) => {
      return new Promise((resolve) => {
        let totalPages = Math.floor(response.data.total / PAGE_SIZE)
        if (response.data.total % PAGE_SIZE > 0) {
          totalPages++
        }

        const totalItems = response.data.total

        resolve({
          items: response.data.items,
          totalPages,
          totalItems
        })
      })
    })
  }

  const fetchSingle = (entity, id) => {
    const prom = axios.get(`/${entity}/${id}`)

    return prom.then((response) => {
      return new Promise((resolve) => {
        resolve(response.data)
      })
    })
  }

  this.fetchAudiobooks = (searchTerm, unabridgedOnly, page) => {
    return fetch("albums", {
      s: searchTerm,
      unabridged_only: unabridgedOnly,
    }, page)
  }

  this.fetchSingleAudiobook = (id) => {
    return fetchSingle("albums", id)
  }

  this.fetchData = () => {
    fetch()
  }

  this.fetchAuthors = () => { }
}

API.install = function (Vue) {
  Vue.prototype.$api = new API();
};

export default API