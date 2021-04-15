import axios from "axios"

const PAGE_SIZE = 5
const API_PATH = "/api"

const API = function () {
  this._cache = []

  const fetch = (entity, params, page) => {
    params = params || {};
    params.offset = page * PAGE_SIZE;
    params.limit = PAGE_SIZE

    const prom = axios.get(`${API_PATH}/${entity}`, { params })

    return prom.then((response) => {
      let totalPages = Math.floor(response.data.total / PAGE_SIZE)
      if (response.data.total % PAGE_SIZE > 0) {
        totalPages++
      }

      const totalItems = response.data.total

      return {
        items: response.data.items,
        totalPages,
        totalItems
      }
    })
  }

  const fetchSingle = (entity, id) => {
    const prom = axios.get(`${API_PATH}/${entity}/${id}`)

    return prom.then((response) => {
      return response.data
    })
  }

  this.fetchAudiobooks = (searchTerm, unabridgedOnly, page) => {
    const regex = /(?:artistID:"([^"]*)")?\s*(.*)/;

    const searchParams = {
      unabridged_only: unabridgedOnly,
    }

    const match = searchTerm.match(regex)
    console.log(match)

    const artistID = match[1]
    searchTerm = match[2]
    if (artistID) { // check whether an artistID is given
      searchParams.artist_id = artistID
    }

    searchParams.s = searchTerm

    return fetch("albums", searchParams, page)
  }

  this.fetchSingleAudiobook = (id) => {
    return fetchSingle("albums", id)
  }

  this.fetchAuthors = (searchTerm, page) => {
    return fetch("artists", {
      s: searchTerm
    }, page)
  }
}

API.install = function (Vue) {
  Vue.prototype.$api = new API();
};

export default API