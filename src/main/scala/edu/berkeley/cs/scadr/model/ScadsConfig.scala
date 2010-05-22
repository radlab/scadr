package edu.berkeley.cs.scadr.model

import org.apache.avro.specific.SpecificRecordBase
import edu.berkeley.cs.scads.storage.{Namespace, TestScalaEngine}
import edu.berkeley.cs.scads.piql.Environment

import piql._

object ScadsEnv {
  val env: Environment = Configurator.configure(TestScalaEngine.cluster)
}
