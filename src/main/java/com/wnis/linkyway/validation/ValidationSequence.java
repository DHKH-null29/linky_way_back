package com.wnis.linkyway.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

import static com.wnis.linkyway.validation.ValidationGroup.*;

@GroupSequence({
        Default.class,
        NotBlankGroup.class,
        PatternCheckGroup.class
})
public interface ValidationSequence {
}
