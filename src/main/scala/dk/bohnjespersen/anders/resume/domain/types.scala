package dk.bohnjespersen.anders.resume.domain

object types {
  opaque type Lang = String

  private val EN      = "en"
  private val Allowed = Set(
    EN,
    "de",
  )
  object Lang:
    val en                                 = Lang.safeOrEnglish("en")
    val de                                 = Lang.safeOrEnglish("de")
    def safe(value: String): Option[Lang]  = if (Allowed.contains(value)) Some(value) else None
    def safeOrEnglish(value: String): Lang = safe(value).getOrElse(EN)
  opaque type Aspect = String

  private val SWE     = "swe"
  private val SDE     = "sde"
  private val AllowedAspects = Set(
    SWE,
    SDE,
  )
  object Aspect:
    val swe                                 = Aspect.safeOrSwe("swe")
    val sde                                 = Aspect.safeOrSwe("sde")
    def safe(value: String): Option[Aspect] = if (AllowedAspects.contains(value)) Some(value) else None
    def safeOrSwe(value: String): Aspect    = safe(value).getOrElse(SWE)
}
