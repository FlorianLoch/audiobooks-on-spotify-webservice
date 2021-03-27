'use strict'

function updateProperties(from, into) {
  let changed = false

  for (let propKey in from) {
    const propVal = from[propKey]

    if (propVal != into[propKey]) {
      changed = true
    }

    into[propKey] = propVal
  }

  return changed
}

export { updateProperties }