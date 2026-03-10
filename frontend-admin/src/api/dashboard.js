import request from '@/utils/request'

export function getStatistics() {
  return request.get('/dashboard/statistics')
}
