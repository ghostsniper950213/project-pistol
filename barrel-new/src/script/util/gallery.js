import defaults from '@/script/data/defaults'

export function calcCatByScore(score) {
  let categories = defaults.categories
  let searchCategories = categories.map(category => category.name)
  categories
    .sort((prev, next) => next.score - prev.score)
    .forEach(category => {
      if (category.score <= score) {
        score -= category.score
        searchCategories.splice(searchCategories.indexOf(category.name), 1)
      }
    })
  return searchCategories
}