package shard

trait Aggregate[V,U] {
  case class Set(value: V)
  case class Update(update: U)

  def update(last: V, up: U): V
}

case class Mean[N](total: N, count: Long)(implicit num: Numeric[N])

object Aggregate {
  class Comparison[N](val replace: (N,N) => Boolean)(implicit num: Numeric[N]) extends Aggregate[N,N] {
    def update(last: N, up: N) = if(replace(last,up)) up else last
  }
  class Max[N](implicit num: Numeric[N]) extends Comparison[N](num.gt _)
  class Min[N](implicit num: Numeric[N]) extends Comparison[N](num.lt _)

  class Mean[N](implicit num: Numeric[N]) extends Aggregate[shard.Mean[N],N] {
    def update(last: shard.Mean[N], up: N) = shard.Mean(num.plus(last.total, up), last.count + 1)
  }  

  class Sum extends Aggregate[Long,Long] {
    def update(last: Long, up: Long) = last + up
  }

  class Count extends Sum
}